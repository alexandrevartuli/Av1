
/*
Função:
RTTaskManager gerencia todos os TaskInfo e TaskChain do conjunto de tarefas. Permite adicionar tarefas
(cada uma com seu TaskInfo e TaskChain), recuperá-las por vehicleId e verificar escalonabilidade com base nas fórmulas de tempo real.

Como se integra ao código externo:
O RTTaskManager é instanciado no RaceView e mantido em uma referência estática acessível por RaceView.getRTManager().
Quando o Car precisa verificar o deadline, chama getRTManager().getTaskInfoByVehicleId(...).
No startRace(), o RTTaskManager é usado para verificar se o conjunto é escalonável antes de iniciar
 as threads. Caso não seja, ajusta-se prioridades via rtManager.adjustPrioritiesIfNeeded().

Essa integração permite que o código do Car (fora da biblioteca) acesse facilmente os parâmetros de escalonamento (D_i, C_i, etc.) e o RaceView use o RTTaskManager para preparar o cenário, tudo sem acoplar a lógica de escalonamento diretamente nas classes do aplicativo.
 */



package com.oficial.rtlib;

import java.util.ArrayList;
import java.util.List;

public class RTTaskManager {
    private List<TaskInfo> tasks;
    private List<TaskChain> taskChains;

    public RTTaskManager() {
        tasks = new ArrayList<>();
        taskChains = new ArrayList<>();
    }

    public void addTask(TaskInfo ti, TaskChain chain) {
        tasks.add(ti);
        taskChains.add(chain);
    }

    public boolean areAllSchedulable() {
        for (TaskInfo ti : tasks) {
            if (!RealTimeScheduler.isTaskSchedulable(ti, tasks)) {
                return false;
            }
        }
        return true;
    }

    public void adjustPrioritiesIfNeeded() {
        for (TaskInfo ti : tasks) {
            if (!RealTimeScheduler.isTaskSchedulable(ti, tasks)) {
                // Aumenta prioridade (quanto menor o número, maior a prioridade)
                ti.priority = Math.max(1, ti.priority - 1);
            }
        }
    }

    public double getUtilization() {
        return RealTimeScheduler.calculateUtilization(tasks);
    }

    // Em RTTaskManager.java
    public TaskInfo getTaskInfoByVehicleId(int vehicleId) {
        for (int i = 0; i < tasks.size(); i++) {
            TaskInfo ti = tasks.get(i);
            if (ti.vehicleId == vehicleId) {
                return ti;
            }
        }
        return null; // se não encontrar
    }



    // Exemplo: Se uma tarefa está atrasada, pode alterar velocidade do carro associado
    // Isso você fará no RaceView, obtendo referência ao Car. Aqui é só lógica.
    public List<TaskInfo> getTasks() {
        return tasks;
    }
}
