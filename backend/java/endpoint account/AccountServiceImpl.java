package br.com.oauth.account;

import br.com.buzz.shared.BaseFilter;
import br.com.buzz.shared.Response;
import br.com.commons.jpa.querys.Criteria;
import br.com.commons.jpa.querys.QueryPagination;
import br.com.commons.jpa.querys.QueryRepository;
import br.com.commons.jpa.service.BaseService;
import br.com.jfunk.Has;
import br.com.jfunk.utils.ModelUtils;
import br.com.jfunk.utils.Utils;
import br.com.models.*;
import br.com.oauth.config.OauthUtil;
import br.com.oauth.entity.Account;
import br.com.oauth.entity.Establishment;
import br.com.oauth.entity.Permission;
import br.com.oauth.entity.Profile;
import br.com.oauth.establishment.EstablishmentRepository;
import br.com.oauth.exception.EntityNotFoundException.EntityType;
import br.com.oauth.exception.ExistingEntityException;
import br.com.oauth.feignclient.EmailFeignClient;
import br.com.oauth.profile.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl extends BaseService<Account, AccountDTO, AccountRepository> implements AccountService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);
    @Autowired
    @Named("accountsQueryRepository")
    private QueryRepository<AccountDTO> accountsQueryRepository;
    @Autowired
    private EstablishmentRepository establishmentRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private EmailFeignClient emailFeignClient;

    @Override
    public AccountDTO save(AccountDTO dto) throws Exception {
        if (!Has.content(dto.getId())) {

            Optional<Account> accountOptional = repository.findByUserName(dto.getUserName());
            accountOptional.ifPresent(account -> {
                throw new ExistingEntityException(EntityType.USER);
            });

            dto.setToken(Utils.getRandomToken());
            LocalDateTime expiration = Utils.nowTime().plusHours(24);
            dto.setTokenExpirationDate(expiration);

            Account entity = accountDTOToEntity(dto);

            entity.setPassWord(new BCryptPasswordEncoder().encode(dto.getPassWord()));
            entity.setEstablishment(establishmentRepository.findById(entity.getEstablishment().getId()).get());
            entity = repository.save(entity);

            EmailRequest emailRequest = new EmailRequest();

            emailRequest.setToEmail(entity.getUsername());
            emailRequest.setBody("Seu usúrio já foi criado, cadastre a sua senha no link: http://www.dominio.com.br/nova-senha?token=" + dto.getToken());
            emailRequest.setContentType("text");
            emailRequest.setSubject("Usuario criado!");

            try {
                emailFeignClient.sendEmail("Bearer " + OauthUtil.getToken(), emailRequest);
            } catch (Exception e) {
                LOGGER.error("Erro ao enviar email: {}", e);
            }

            return accountEntityToDTO(entity);

        } else {
            Account entity = accountDTOToEntity(dto);
            entity.setEstablishment(establishmentRepository.findById(entity.getEstablishment().getId()).get());
            entity = repository.save(entity);
            return accountEntityToDTO(entity);
        }

    }

    @Override
    public void recoverPassword(String username) throws Exception {
        Optional<Account> accountOptional = repository.findByUserName(username);
        if (!accountOptional.isEmpty()) {

            accountOptional.ifPresent(account -> {
                account.setToken(Utils.getRandomToken());
                LocalDateTime expiration = Utils.nowTime().plusHours(24);
                account.setTokenExpirationDate(expiration);
                account = repository.save(account);

                EmailRequest emailRequest = new EmailRequest();

                emailRequest.setToEmail(account.getUsername());
                emailRequest.setBody("Olá, cadastre uma nova senha para o usuário " + account.getUsername() + " no link: http://www.dominio.com.br/nova-senha?token=" + account.getToken());
                emailRequest.setContentType("text");
                emailRequest.setSubject("Recuperação de senha.");

                try {
                    emailFeignClient.sendEmail("Bearer " + OauthUtil.getToken(), emailRequest);
                } catch (Exception e) {
                    LOGGER.error("Erro ao enviar email: {}", e);
                }
            });

        } else {
            throw ACCOUNT_NOT_FOUND;
        }
    }

    @Override
    public void newPassword(String token, String newPassword) throws Exception {

        Optional<Account> accountOptional = repository.findByToken(token);
        if (!accountOptional.isEmpty()) {
            if (isValidToken(token)) {
                accountOptional.ifPresent(account -> {
                    account.setToken("");
                    account.setTokenExpirationDate(null);
                    account.setPassWord(new BCryptPasswordEncoder().encode(newPassword));
                    account = repository.save(account);
                });
            } else {
                throw REQUEST_EXPIRED;
            }
        } else {
            throw REQUEST_NOT_FOUND;
        }
    }

    private Boolean isValidToken(String token) {
        Optional<Account> accountOptional = repository.findByToken(token);
        if (!accountOptional.isEmpty()) {
            if (Utils.nowTime().isAfter(accountOptional.get().getTokenExpirationDate())) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public AccountDTO findById(String publicId) {
        Long entityId = ModelUtils.publicIdToEntityId(publicId);
        Optional<Account> accountOptional = repository.findById(entityId);
        if (accountOptional.isPresent()) {
            return accountEntityToDTO(accountOptional.get());
        }
        throw new IllegalArgumentException("Resource not found for id: " + publicId);
    }

    @Override
    public Response<AccountDTO> list(BaseFilter baseFilter, Long establishmentId) {
        Criteria criteria = new Criteria();
        criteria.addParameter("search", baseFilter.getSearch());

        if (Has.content(establishmentId)) {
            criteria.addParameter("establishmentId", establishmentId);
        }

        Response<AccountDTO> results = QueryPagination.getPagination(baseFilter, accountsQueryRepository, criteria);
        results.setContent(results.getContent().stream().map(r -> {
            r.setId(ModelUtils.entityIdToPublicId(Long.valueOf(r.getId())));
            return r;
        }).collect(Collectors.toList()));
        return results;
    }

    @Override
    public AccountDTO newUserAccount(NewUserAccountRequest user) throws Exception {

        EstablishmentDTO establishmentDTO = new EstablishmentDTO();
        establishmentDTO.setId(entityIdToPublicId(user.getEstablishmentId()));

        AccountDTO accountDTO = AccountDTO.builder()
                .userName(user.getUserName())
                .active(true)
                .establishment(establishmentDTO)
                .passWord("123")
                .profiles(user.getProfiles())
                .permissions(user.getPermissions())
                .build();

        return save(accountDTO);

    }

    @Override
    public void enableDisable(String publicId) {
        Long entityId = ModelUtils.publicIdToEntityId(publicId);
        Optional<Account> accountOptional = repository.findById(entityId);
        accountOptional.get().setActive(!accountOptional.get().getActive());
        accountOptional.ifPresent(account -> repository.save(account));
    }

    private Account accountDTOToEntity(AccountDTO dto) {

        Optional<Establishment> establishmentOptional = establishmentRepository.findById(ModelUtils.publicIdToEntityId(dto.getEstablishment().getId()));

        Set<Permission> permissionsEntity;
        permissionsEntity = new HashSet<Permission>();
        dto.getPermissions().forEach(permissionDTO -> {
            Permission permission = permissionDTOToEntity(permissionDTO);
            permissionsEntity.add(permission);
        });

        Set<Profile> profilesEntity;
        profilesEntity = new HashSet<Profile>();
        dto.getProfiles().forEach(profileDTO -> {
            Profile profile = profileDTOToEntity(profileDTO);
            profilesEntity.add(profile);
        });

        Account result = Account.builder().id(ModelUtils.publicIdToEntityId(dto.getId()))
                .active(dto.getActive())
                .userName(dto.getUserName())
                .passWord(dto.getPassWord())
                .token(dto.getToken())
                .tokenExpirationDate(dto.getTokenExpirationDate())
                .establishment(establishmentOptional.get())
                .profiles(profilesEntity)
                .permissions(permissionsEntity)
                .revision(dto.getRevision())
                .build();

        return result;
    }

    private Profile profileDTOToEntity(ProfileDTO dto) {

        Set<Permission> permissionsEntity;
        permissionsEntity = new HashSet<Permission>();
        dto.getPermissions().forEach(permissionDTO -> {
            Permission permission = permissionDTOToEntity(permissionDTO);
            permissionsEntity.add(permission);
        });

        Profile result = Profile.builder().id(ModelUtils.publicIdToEntityId(dto.getId()))
                .establishmentId(ModelUtils.publicIdToEntityId(dto.getEstablishmentId()))
                .name(dto.getName())
                .permissions(permissionsEntity)
                .revision(dto.getRevision())
                .build();

        return result;
    }

    private Permission permissionDTOToEntity(PermissionDTO dto) {

        Permission result = Permission.builder().id(ModelUtils.publicIdToEntityId(dto.getId()))
                .description(dto.getDescription())
                .permission(dto.getPermission())
                .revision(dto.getRevision())
                .build();

        return result;
    }

    private AccountDTO accountEntityToDTO(Account entity) {

        Optional<Establishment> establishmentOptional = establishmentRepository.findById(entity.getEstablishment().getId());
        EstablishmentDTO establishmentDTO = new EstablishmentDTO();
        if (establishmentOptional.isPresent()) {
            establishmentDTO = establishmentEntityToDTO(establishmentOptional.get());
        }

        Set<PermissionDTO> permissionsDTO;
        permissionsDTO = new HashSet<PermissionDTO>();
        entity.getPermissions().forEach(permissionEntity -> {
            PermissionDTO permissionDTO = permissionEntityToDTO(permissionEntity);
            permissionsDTO.add(permissionDTO);
        });

        Set<ProfileDTO> profilesDTO;
        profilesDTO = new HashSet<ProfileDTO>();
        entity.getProfiles().forEach(profileEntity -> {
            ProfileDTO profileDTO = profileEntityToDTO(profileEntity);
            profilesDTO.add(profileDTO);
        });

        AccountDTO result = AccountDTO.builder().id(ModelUtils.entityIdToPublicId(entity.getId()))
                .active(entity.getActive())
                .userName(entity.getUsername())
                .passWord(entity.getPassword())
                .token(entity.getToken())
                .tokenExpirationDate(entity.getTokenExpirationDate())
                .establishment(establishmentDTO)
                .profiles(profilesDTO)
                .permissions(permissionsDTO)
                .revision(entity.getRevision())
                .build();

        return result;
    }

    private ProfileDTO profileEntityToDTO(Profile entity) {
        Set<PermissionDTO> permissionsDTO;
        permissionsDTO = new HashSet<PermissionDTO>();
        entity.getPermissions().forEach(permissionEntity -> {
            PermissionDTO permissionDTO = permissionEntityToDTO(permissionEntity);
            permissionsDTO.add(permissionDTO);
        });

        ProfileDTO result = ProfileDTO.builder().id(ModelUtils.entityIdToPublicId(entity.getId()))
                .establishmentId(ModelUtils.entityIdToPublicId(entity.getEstablishmentId()))
                .name(entity.getName())
                .permissions(permissionsDTO)
                .revision(entity.getRevision())
                .build();

        return result;
    }

    private PermissionDTO permissionEntityToDTO(Permission entity) {
        PermissionDTO result = PermissionDTO.builder().id(ModelUtils.entityIdToPublicId(entity.getId()))
                .description(entity.getDescription())
                .permission(entity.getPermission())
                .revision(entity.getRevision())
                .build();
        return result;
    }

    private EstablishmentDTO establishmentEntityToDTO(Establishment entity) {
        EstablishmentDTO result = EstablishmentDTO.builder().id(ModelUtils.entityIdToPublicId(entity.getId()))
                .active(entity.getActive())
                .documentNumber(entity.getDocumentNumber())
                .build();
        return result;
    }

}
