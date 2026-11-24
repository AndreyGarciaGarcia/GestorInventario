import { ApplicationConfig, provideBrowserGlobalErrorListeners, LOCALE_ID } from '@angular/core'
import { provideHttpClient, withFetch } from '@angular/common/http'

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    // HttpClient con fetch: mismo transporte que el resto del proyecto
    provideHttpClient(withFetch()),
    // Idioma por defecto de la app (números, monedas, fechas)
    { provide: LOCALE_ID, useValue: 'es-ES' },
  ],
}
