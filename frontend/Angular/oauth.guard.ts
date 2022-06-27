import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { OAuthService } from './oauth.service';

@Injectable()
export class OAuthGuard implements CanActivate {

  constructor(private oauthService: OAuthService,
              private router: Router) {}

  canActivate(next: ActivatedRouteSnapshot,
              state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    if (!this.oauthService.isLogado() && state.url !== '/login' ) {
      this.router.navigate(['/login']);
      return false;
    }

    return true;
  }

}