package br.com.oauth.exception;

import java.util.stream.Collectors;
import java.util.stream.Stream;


public class EntityNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(EntityType entityType, String... arguments) {
        super("Nao foi possivel encontrar o(a) " + entityType + " na base atraves dos parametros " + Stream.of(arguments).collect(Collectors.joining(",")));
    }

    public enum EntityType {
        ESTABLISHMENT("estabelecimento comercial"),
        USER("usuário"),
        PROFILE("perfil"),
        CUSTOMER("cliente");
        String type;
        private EntityType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

}
