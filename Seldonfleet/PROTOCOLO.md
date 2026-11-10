# Protocolo de classificação

## Regra adotada

Usamos os limiares de referência, definidos antes de calcular os resultados da frota. O dreno relativo é comparável entre capacidades diferentes. Primeiro convertemos consumo em mWh para mAh: energia em mAh = energia em mWh / tensão nominal em volts. Energia já em mAh permanece inalterada. Depois:

- Dreno absoluto = energia em mAh / (minutos de tela / 60), em mAh/h.
- Dreno relativo = dreno absoluto / capacidade em mAh × 100, em %/h.

As condições são avaliadas nesta ordem, com comparações inclusivas:

1. **Indeterminado:** capacidade ou tempo de tela ausente ou não positivo; energia ausente, negativa, não finita, unidade desconhecida, ou tensão ausente/não positiva/não finita quando necessária; jank ou falhas ausentes ou inválidos; ou resultado aritmético não finito.
2. **Crítico:** dreno ≥ 12%/h, ou falhas em 7 dias ≥ 3, ou quadros perdidos ≥ 15%.
3. **Atenção:** dreno ≥ 7%/h, ou falhas ≥ 1, ou quadros perdidos ≥ 8%.
4. **OK:** nenhum dos casos anteriores.

Indeterminado prevalece mesmo quando outro sinal é crítico. Dentro do mesmo estado, a justificativa apresenta o primeiro sinal na ordem dreno, falhas, quadros perdidos. A classificação usa valores sem arredondamento; apenas a exibição arredonda para uma casa decimal.

## Tratamento dos defeitos

Consumos mWh são divididos por nominalVoltage antes de comparar ou classificar. Capacidade zero ou ausente e tempo de tela zero ou ausente não são usados como divisores: o dispositivo permanece na lista como **Sem dados** e o detalhe explica a causa. Não há substituição por zero, NaN ou Infinity. Se a entrada estrutural do arquivo estiver inválida, a tela informa falha de leitura.

## Ameaças à validade

O dreno pressupõe que todo o consumo registrado ocorreu durante o tempo de tela informado. Consumo em segundo plano pode superestimar a taxa e gerar falso crítico; além disso, os limiares são heurísticos e não foram calibrados com diagnóstico real. A data de último contato não integra a regra, portanto telemetria antiga ainda pode ser classificada.
