# language: pt
  Funcionalidade: Pagamento
    Cenario: Gerar um QrCode de pagamento
      Quando solicitar geração do qrcode
      Entao deve receber status 200
      E exibir a imagem QrCode