import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OAuthService } from './oauth.service';

export const INTERCEPTOR_SKIP_HEADER = 'X-Skip-Interceptor';
export const MULTIPART_DATA = 'MULTIPART_DATA';


@Injectable()
export class RequestInterceptorService implements HttpInterceptor {

  headers: HttpHeaders = new HttpHeaders();

  constructor(private oauthService: OAuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const hasSkipHeader: boolean = req.headers.has(INTERCEPTOR_SKIP_HEADER);
    const token: string = localStorage.getItem('token');

    if (token && !hasSkipHeader && !this.verifyIsAuthetication(req)) {

      this.requestNewToken();

      req = req.clone({
        setHeaders: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      });

    } else if (hasSkipHeader) {

      this.headers = req.headers.delete(INTERCEPTOR_SKIP_HEADER);
      req = req.clone({
        headers: this.headers
      });
    }

    return next.handle(req);
  }

  verifyIsAuthetication(req: HttpRequest<any>): boolean {

    if (req.url.includes('/oauth/token')) {
      return true;
    }

    return false;
  }

  requestNewToken(): void {
    this.oauthService.getNewAccessToken();
  }
}
