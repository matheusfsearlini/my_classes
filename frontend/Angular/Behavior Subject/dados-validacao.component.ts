import { Component, OnInit } from '@angular/core';
import { ValidarCompradorLinkRequest } from 'src/app/shared/models/validar-comprador-link-request';
import { PagamentoLinkService } from '../shared/pagamento-link.service';

@Component({
  selector: 'app-dados-validacao',
  templateUrl: './dados-validacao.component.html',
  styleUrls: ['./dados-validacao.component.css']
})
export class DadosValidacaoComponent implements OnInit {

  dadosUsuario: ValidarCompradorLinkRequest;

  constructor(private pagamentoLinkService: PagamentoLinkService) { }

  ngOnInit(): void { }

  botaoValidarUsuario() {
    this.pagamentoLinkService.validarUsuario(this.dadosUsuario);
  }

}
