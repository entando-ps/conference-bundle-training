import {
  Component,
  Input,
  NgZone,
  OnDestroy,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import { environment } from '../environments/environment.dev';
import { Config } from './models/config.model';
import { ApiService } from './services/api.service';
import { KeycloakService } from './services/keycloak.service';
import { Subject, filter, switchMap, take, takeUntil } from 'rxjs';
import { mediatorInstance } from '@entando/mfecommunication';
import { Conference } from './models/conference.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [],
  templateUrl: './app.component.html',
  styleUrls: ['../styles.css', './app.component.css'],
  encapsulation: ViewEncapsulation.ShadowDom,
})
export class AppComponent implements OnInit, OnDestroy {
  @Input() config!: Config | string;

  public conferences: Conference[] = [];
  private ondestroy$: Subject<void> = new Subject();

  constructor(
    private apiService: ApiService,
    private keycloakService: KeycloakService,
    private ngZone: NgZone
  ) {
    mediatorInstance.subscribe('conference.created', {
      callerId: 'conference-table',
      callback: (conference) => {
        this.ngZone.run(() => {
          console.log('Received conference', conference);
          this.getConferences();
        });
      },
    });
  }

  public ngOnInit(): void {
    this.setConfig();
    this.getConferences();
  }

  public ngOnDestroy(): void {
    this.ondestroy$.next();
    this.ondestroy$.complete();
    mediatorInstance.unsubscribe('conference.created', 'conference-table');
  }

  public getConferences(): void {
    this.keycloakService.instance$
      .pipe(
        takeUntil(this.ondestroy$),
        filter((keycloak) => keycloak.initialized),
        take(1),
        switchMap(() => this.apiService.getAllConferences())
      )
      .subscribe((conferences: Conference[]) => {
        this.conferences = conferences;
      });
  }

  private setConfig(): void {
    if (typeof this.config === 'string') this.config = JSON.parse(this.config);
    else this.config = environment;

    const config = this.config as Config;
    this.apiService.url = config.systemParams.api['conference-ms-claim'].url;
  }
}
