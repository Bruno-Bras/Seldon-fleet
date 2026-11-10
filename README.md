# Seldon Fleet

Aplicativo Android offline para inspecionar a saúde de uma frota local. Consulte [PROTOCOLO.md](PROTOCOLO.md) para fórmulas, precedência, defeitos e limitações.

## Executar

Abra o projeto no Android Studio com JDK 17 e SDK Android 35. O arquivo fornecido está em `app/src/main/assets/frota.json`; substitua-o mantendo a estrutura documentada no desafio para atualizar a frota. Execute o app em Android 7.0 ou superior.

```sh
./gradlew assembleDebug
./gradlew test
./gradlew lintDebug
```

A lista oferece contagens globais, filtro por estado e favoritos. Toque em um dispositivo para ver telemetria e justificativa. Favoritos são guardados com DataStore; o arquivo é relido ao iniciar o processo. Não há permissão de rede.

## Decisões e próximos passos

A injeção é manual; a interface do repositório e a regra são independentes de Android. Dados insuficientes continuam visíveis em vez de serem descartados. O cálculo usa precisão completa e a apresentação arredonda. O arquivo inválido gera uma mensagem de erro, não uma lista silenciosamente incompleta.

Como próximos passos, validar acessibilidade e layouts em mais dispositivos, acrescentar testes de interface e calibrar limiares com dados de diagnóstico reais. A execução remota da CI depende da publicação do repositório.

## Capturas

As capturas da lista e do detalhe devem ser adicionadas após executar o aplicativo em um dispositivo ou emulador.

## Uso de IA

Um assistente de IA foi usado para estruturar e implementar o aplicativo, testes e documentação a partir da especificação. As decisões e o código devem ser revisados e compreendidos pelo autor antes da entrega e da entrevista.
