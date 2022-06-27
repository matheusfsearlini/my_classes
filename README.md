# Minhas classes

Repositório criado para documentar como foi implementado por mim alguma metodologia ou codigo especifico.

Front-end
Framework Angular 2+
  
  arquivos:
  
    -base.service.ts: service com requests genericas.
  
    -oauth.guard.ts: classe com guarda de rota.
  
    -request-interceptor.service.ts:  interceptador de requisições (token, headers).
  
  pastas:
  
    -Behavior Subject: Criado comunicação entre components irmãos atravez de um service.
    
    -Crud: Exemplo de cadastro, usando PrimeNg, Bootstrap e awesome icons.
    
Back-end

    Java: Usado java 8, maven, spring jpa e flyway.
      -endpoint account: Controller criado para cadastrar, atualizar, listar, inativar ou buscar por id usuário.
      -exception handler: Classes criadas para tratar erros.
    
    Kotlin: Usado Kotlin, gradle e spring jpa.
      crud: Controller criado para cadastrar, atualizar, listar, buscar por id ou deletar usuário.
      service bus: implementação de serviço de filas do Microsoft Azure
        -publisher: Service usado para escrever na fila.
        -subscribe: Component usado para ler a fila
      
    
