package br.com.oauth.account;

import br.com.buzz.shared.BaseFilter;
import br.com.buzz.shared.Response;
import br.com.models.AccountDTO;
import br.com.models.NewUserAccountRequest;
import br.com.models.utils.StringResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public Response<AccountDTO> findAll(
            @ModelAttribute BaseFilter filter,
            @RequestParam(name = "establishmentId", required = false) Long establishmentId) {
        return accountService.list(filter, establishmentId);
    }

    @GetMapping("/{id}")
    public AccountDTO findById(@PathVariable("id") String id) {
        return accountService.findById(id);
    }

    @PostMapping
    public AccountDTO save(@Valid @RequestBody AccountDTO dto) throws Exception {
        return accountService.save(dto);
    }

    @PostMapping("/new-account")
    public AccountDTO newAccount(@Valid @RequestBody NewUserAccountRequest user) throws Exception{
        return accountService.newUserAccount(user);
    }

    @PatchMapping("/new-password/{token}")
    public ResponseEntity<StringResponse> newPassword(@PathVariable("token") String token,
                                                      @RequestParam("password") String newPassword) {
        try {
            accountService.newPassword(token, newPassword);
            return new ResponseEntity<StringResponse>(new StringResponse("OK"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<StringResponse>(new StringResponse(e.getMessage()),
                    HttpStatus.PRECONDITION_FAILED);
        }
    }

    @PatchMapping("/recover-password/{username}")
    public ResponseEntity<StringResponse> newPassword(@PathVariable("username") String username) {
        try {
            accountService.recoverPassword(username);
            return new ResponseEntity<StringResponse>(new StringResponse("OK"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<StringResponse>(new StringResponse(e.getMessage()),
                    HttpStatus.PRECONDITION_FAILED);
        }
    }

    @PutMapping("/{id}")
    public void enableDisable(@PathVariable("id") String id) {
        accountService.enableDisable(id);
    }
}
