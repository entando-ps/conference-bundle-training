import 'zone.js';
import { provideHttpClient, withInterceptors } from "@angular/common/http";
import { createCustomElement } from "@angular/elements";
import { createApplication } from "@angular/platform-browser";
import { AppComponent } from "./app/app.component";
import { SecurityInterceptor } from "./app/interceptors/security.interceptor";

(async () => {
  const app = await createApplication({
    providers: [
      provideHttpClient(withInterceptors([SecurityInterceptor]))
    ],
  })


  const element = createCustomElement(AppComponent, {
    injector: app.injector
  })

  customElements.define('ng-conference-form', element);
})();