package br.com.oauth.account;

import br.com.buzz.shared.BaseFilter;
import br.com.buzz.shared.Response;
import br.com.models.AccountDTO;
import br.com.models.NewUserAccountRequest;

public interface AccountService {

    AccountDTO save(AccountDTO dto) throws Exception;

    AccountDTO findById(String publicId);

    Response<AccountDTO> list(BaseFilter filtro, Long establishmentId);

    void recoverPassword(String username) throws Exception;

    void newPassword(String token, String newPassword) throws Exception;

    AccountDTO newUserAccount(NewUserAccountRequest user) throws Exception;

    void enableDisable(String publicId);

    Exception REQUEST_EXPIRED = new Exception("Solicitação para troca de senha expirada!");

    Exception REQUEST_NOT_FOUND = new Exception("Solicitação para troca de senha não encontrada!");

    Exception ACCOUNT_NOT_FOUND = new Exception("Usuário não encontrado.");

    Exception USERNAME_IN_USE = new Exception("Nome de usuário em uso.");
}
