/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_URL?: string;
  /** @deprecated Use VITE_API_URL. Kept for existing local installs. */
  readonly VITE_API_BASE_URL?: string;
}
