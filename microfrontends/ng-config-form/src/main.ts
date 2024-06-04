import 'zone.js';
import { provideHttpClient, withInterceptors } from "@angular/common/http";
import { createCustomElement } from "@angular/elements";
import { createApplication } from "@angular/platform-browser";
import { AppComponent } from "./app/app.component";

(async () => {
  const app = await createApplication({
    providers: [],
  })


  const element = createCustomElement(AppComponent, {
    injector: app.injector
  })

  customElements.define('ng-config-form', element);
})();