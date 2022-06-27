import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

@Injectable()
export class BaseService<T> {

  public BASE_URL = environment.backEndUrl //micro-serviço
  URL: string //endpoint

  constructor(protected http: HttpClient) { }

  generatorHeaders() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    });
    return headers;
  }

  getAll() {
    return this.http.get<T[]>(`${this.BASE_URL}${this.URL}`,).pipe(
      retry(3),
      catchError(this.handleError)
    );
  }

  getById(id: string): Observable<T> {
    return this.http.get<T>(`${this.BASE_URL}${this.URL}/${id}`,)
      .pipe(
        retry(1),
        catchError(this.handleError)
      )
  }

  save(object: T): Observable<T> {
    return this.http
      .post<T>(
        this.BASE_URL + this.URL,
        JSON.stringify(object),

      )
      .pipe(retry(1), catchError(this.handleError));
  }

  delete(id: string) {
    return this.http
      .delete<T>(`${this.BASE_URL}${this.URL}/${id}`,)
      .pipe(retry(1), catchError(this.handleError));
  }

  update(object: T, id: string): Observable<T> {
    return this.http
      .post<T>(`${this.BASE_URL}${this.URL}/${id}`, JSON.stringify(object),)
      .pipe(retry(1), catchError(this.handleError));
  }

  enableDisable(id: string) {
    return this.http
      .put<T>(`${this.BASE_URL}${this.URL}/${id}`, null)
      .pipe(retry(1), catchError(this.handleError));
  }

  handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      console.error('Ocorreu um erro: ', error.error.message);
    } else {
      console.error(
        `Código retornado de back-end ${error.status}, body é: ${error.error}`
      );
    }
    return throwError('Algo ruím aconteceu, por favor, tente novamente mais tarde.');
  }

}
