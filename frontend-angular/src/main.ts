import { bootstrapApplication } from '@angular/platform-browser'
import { registerLocaleData } from '@angular/common'
import localeEs from '@angular/common/locales/es'

import { appConfig } from './app/app.config'
import { App } from './app/app'

// Datos de locale en español para CurrencyPipe ('es-ES', fechas, números)
registerLocaleData(localeEs)

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err))
