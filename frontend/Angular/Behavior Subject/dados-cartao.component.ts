import { Component, OnInit } from '@angular/core';

import { ValidarCompradorLinkResponse } from 'src/app/shared/models/validar-comprador-link-response';
import { PagamentoLinkService } from '../shared/pagamento-link.service';

@Component({
  selector: 'app-dados-cartao',
  templateUrl: './dados-cartao.component.html',
  styleUrls: ['./dados-cartao.component.css']
})
export class DadosCartaoComponent implements OnInit {

  validarCompradorLinkResponse: ValidarCompradorLinkResponse;

  constructor(private pagamentoLinkService: PagamentoLinkService) { }

  ngOnInit(): void {
    this.pagamentoLinkService.currentValidaUsuario.subscribe(respValidaUsuario => {
      this.validarCompradorLinkResponse = respValidaUsuario;
    })
  }

}
