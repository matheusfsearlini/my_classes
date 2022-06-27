package br.com.oauth.exception;

public class ExistingEntityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExistingEntityException(EntityNotFoundException.EntityType entityType) {
        super("O(a) " + entityType + " em questão já existe na base de dados");
    }


}
