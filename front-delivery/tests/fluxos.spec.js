const { test, expect } = require('@playwright/test');

const PAGINA_INEXISTENTE = 'This page could not be found';

async function abrirArea(page, nomeDoBotao) {
  await page.goto('/');
  await page.getByRole('link', { name: nomeDoBotao }).click();
}

// Os testes abaixo do FE-01 descrevem o comportamento que o sistema em Java já
// oferece no terminal. Eles falham enquanto o front-end não implementar essas telas.

test('FE-01: página inicial mostra o logo e os dois acessos', async ({ page }) => {
  await page.goto('/');

  await expect(page).toHaveTitle('Massa Mia');
  await expect(page.getByRole('img', { name: 'Massa Mia' })).toBeVisible();
  await expect(page.getByRole('link', { name: 'Proprietário' })).toBeVisible();
  await expect(page.getByRole('link', { name: 'Funcionário' })).toBeVisible();
});

test('FE-02: botão Proprietário abre a área do proprietário (F-18)', async ({ page }) => {
  await abrirArea(page, 'Proprietário');

  await expect(page).toHaveURL(/\/proprietario$/);
  await expect(page.getByText(PAGINA_INEXISTENTE)).toHaveCount(0);
});

test('FE-03: botão Funcionário abre a área do funcionário (F-18)', async ({ page }) => {
  await abrirArea(page, 'Funcionário');

  await expect(page).toHaveURL(/\/funcionario$/);
  await expect(page.getByText(PAGINA_INEXISTENTE)).toHaveCount(0);
});

test('FE-04: área do proprietário pede a senha de acesso', async ({ page }) => {
  await abrirArea(page, 'Proprietário');

  await expect(page.getByLabel(/senha/i)).toBeVisible();
});

test('FE-05: área do funcionário oferece o registro de venda', async ({ page }) => {
  await abrirArea(page, 'Funcionário');

  await expect(page.getByRole('button', { name: /registrar venda/i })).toBeVisible();
});
