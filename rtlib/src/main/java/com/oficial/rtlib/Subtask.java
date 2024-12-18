
/*

Função:
Subtask é uma entidade simples representando uma operação individual com tempo de execução estimado (por exemplo, 5ms para leitura de sensor). É a unidade básica dentro do TaskChain.

Como se integra ao código externo:
Ao criar as tarefas no RaceView, define-se várias Subtask (A, B, C) com seus tempos. Esses Subtask ficam apenas dentro do TaskChain.
 No Car, esses tempos são reproduzidos com Thread.sleep(), simulando o custo real de cada etapa da tarefa.
 Assim, Subtask ajuda a tornar o modelo mais realista, mostrando dependências internas e como o C total da tarefa é composto.

 */



package com.oficial.rtlib;

public class Subtask {
    public String name;
    public int executionTime; // ms

    public Subtask(String name, int executionTime) {
        this.name = name;
        this.executionTime = executionTime;
    }
}
