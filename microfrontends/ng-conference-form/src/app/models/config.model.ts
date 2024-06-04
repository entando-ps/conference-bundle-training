export interface Config {
  systemParams: SystemParams;
}

export interface SystemParams {
  api: Api;
}

export interface Api {
  'conference-ms-claim': ConferenceMsClaim;
}

export interface ConferenceMsClaim {
  url: string;
}
