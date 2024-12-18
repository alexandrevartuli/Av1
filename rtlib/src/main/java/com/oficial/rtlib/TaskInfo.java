
/*

TaskInfo encapsula os parâmetros de uma tarefa tempo real: J (jitter),
C (custo/tempo de execução), P (período), D (deadline) e prioridade. Cada carro da corrida é mapeado para um TaskInfo, permitindo avaliar suas restrições de tempo.

Como se integra ao código externo:
No RaceView, ao criar os carros, é instanciado um TaskInfo correspondente a cada um. Em seguida,
esse TaskInfo é registrado no RTTaskManager. Depois, na classe Car, ao final de cada ciclo (move()), recupera-se o TaskInfo pelo vehicleId, verificando se o tempo de resposta (R_i) cumpriu o deadline (D_i). Assim, TaskInfo é o elo entre o mundo real (o carro rodando em tempo real) e os parâmetros teóricos de escalonamento definidos no papel (C, P, D, prioridade).
 */

package com.oficial.rtlib;

public class TaskInfo {
    public String taskName;
    public int vehicleId;
    public int J; // Jitter (ms)
    public int C; // Custo computacional (ms)
    public int P; // Período (ms)
    public int D; // Deadline (ms)
    public int priority; // prioridade

    public TaskInfo(String taskName, int vehicleId, int J, int C, int P, int D, int priority) {
        this.taskName = taskName;
        this.vehicleId = vehicleId;
        this.J = J;
        this.C = C;
        this.P = P;
        this.D = D;
        this.priority = priority;
    }
}
