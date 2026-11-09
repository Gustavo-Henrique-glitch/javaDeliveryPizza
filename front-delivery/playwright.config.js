const { defineConfig, devices } = require('@playwright/test');

const PORTA = 34580;
const PASTA_DE_SAIDA = '../build/tests';

module.exports = defineConfig({
  testDir: './tests',
  outputDir: `${PASTA_DE_SAIDA}/classes/frontend`,
  timeout: 15000,
  expect: { timeout: 3000 },
  fullyParallel: true,
  reporter: [
    ['list'],
    ['junit', { outputFile: `${PASTA_DE_SAIDA}/reports/frontend/resultados.xml` }],
  ],
  use: {
    baseURL: `http://127.0.0.1:${PORTA}`,
    screenshot: 'only-on-failure',
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
  webServer: {
    command: `npm run build && npm run start -- -p ${PORTA}`,
    url: `http://127.0.0.1:${PORTA}`,
    timeout: 120000,
    reuseExistingServer: false,
    env: { NEXT_TELEMETRY_DISABLED: '1' },
  },
});
