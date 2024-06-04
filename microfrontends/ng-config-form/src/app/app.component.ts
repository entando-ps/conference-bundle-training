import { Component, Input, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';

interface Config {
  location:string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  @Input() config!: Config | string;


  ngOnInit(): void {
    this.setConfig();
  }

  public onInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.config = { ...(this.config as Config), [target.name]: target.value };
    console.log(this.config);
  }


  private setConfig() {
    if (typeof this.config === 'string') this.config = JSON.parse(this.config);
  }
}
