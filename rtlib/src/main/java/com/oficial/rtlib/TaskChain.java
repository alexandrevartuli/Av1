/*
Função:
TaskChain modela a sequência de subtarefas que compõem a execução de uma tarefa em um ciclo. Por exemplo,
 A->B->C (SensorRead->DBStore->Move). O TaskChain guarda essas subtarefas e seu tempo de execução estimado.

Como se integra ao código externo:
No RaceView, após criar o TaskInfo, um TaskChain é instanciado e preenchido com subtarefas (Subtask).
O TaskChain é então associado ao TaskInfo no RTTaskManager. No código do Car, simula-se a execução dessas subtarefas com Thread.sleep(),
correspondendo ao C total da tarefa. Dessa forma, o TaskChain fornece a estrutura de dependências interna (A depende de B que depende de C) que o Car respeita durante o move().

 */



package com.oficial.rtlib;

import java.util.ArrayList;
import java.util.List;

public class TaskChain {
    public List<Subtask> subtasks;

    public TaskChain() {
        subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask s) {
        subtasks.add(s);
    }

    public int getTotalExecutionTime() {
        int total = 0;
        for (Subtask st : subtasks) {
            total += st.executionTime;
        }
        return total;
    }
}
