import {
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest,
} from '@angular/common/http';

const getKeycloakToken = (): string | null => {
  if (window.entando?.keycloak?.authenticated) {
    return window.entando.keycloak.token;
  }
  return null;
};

export const SecurityInterceptor: HttpInterceptorFn = (
  request: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const token = getKeycloakToken();

  if (token) {
    const headers = {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    };
    request = request.clone({
      setHeaders: headers,
    });
  }

  return next(request);
};
