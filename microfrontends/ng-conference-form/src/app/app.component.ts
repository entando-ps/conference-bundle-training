import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { environment } from '../environments/environment.dev';
import { Config } from './models/config.model';
import { ApiService } from './services/api.service';
import { KeycloakService } from './services/keycloak.service';
import { Subject, filter, switchMap, take, takeUntil } from 'rxjs';
import { mediatorInstance } from '@entando/mfecommunication';
import { Validators } from '@angular/forms';

interface ConferenceForm {
  name: FormControl<string>;
  location: FormControl<string>;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['../styles.css', './app.component.css'],
  encapsulation: ViewEncapsulation.ShadowDom,
})
export class AppComponent implements OnInit {
  @Input() config!: Config | string;

  private ondestroy$: Subject<void> = new Subject();

  constructor(
    private apiService: ApiService,
    private keycloakService: KeycloakService
  ) {}

  public ngOnInit(): void {
    this.setConfig();
  }

  public conferenceForm: FormGroup = new FormGroup<ConferenceForm>({
    name: new FormControl('', {nonNullable: true, validators: [Validators.required]}),
    location: new FormControl('', {nonNullable: true, validators: [Validators.required]}),
  });

  public saveNewConference(): void {
    this.keycloakService.instance$
      .pipe(
        takeUntil(this.ondestroy$),
        filter((keycloak) => keycloak.initialized),
        take(1),
        switchMap(() =>
          this.apiService.saveConference(this.conferenceForm.value)
        )
      )
      .subscribe(() => {
        // This is a custom event that will be listened by the table mfe.
        mediatorInstance.publish('conference.created', this.conferenceForm.value);
        this.conferenceForm.reset();
      });
  }

  private setConfig(): void {
    if (typeof this.config === 'string') this.config = JSON.parse(this.config);
    else this.config = environment;

    const config = this.config as Config;
    this.apiService.url = config.systemParams.api['conference-ms-claim'].url;
  }
}
