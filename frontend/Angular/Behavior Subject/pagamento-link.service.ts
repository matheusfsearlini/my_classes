import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';
import { ValidarCompradorLinkResponse } from 'src/app/shared/models/validar-comprador-link-response';
import { PagamentoService } from 'src/app/shared/services/pagamento.service';

@Injectable({
  providedIn: 'root'
})
export class PagamentoLinkService {

  private validaUsuario = new BehaviorSubject<ValidarCompradorLinkResponse>(new ValidarCompradorLinkResponse());
  currentValidaUsuario = this.validaUsuario.asObservable();

  constructor(
    private pagamentoService: PagamentoService,
    private router: Router,
  ) { }

  validarUsuario(dadosUsuario) {
    this.pagamentoService.validarDadosUsuarioPagamento(dadosUsuario).subscribe(respUsuario => {
      this.validaUsuario.next(respUsuario);
      this.router.navigate([`/pagamento-link/dados-cartao/`]);
    }, error => {
      console.log(error);
    });
  }

}