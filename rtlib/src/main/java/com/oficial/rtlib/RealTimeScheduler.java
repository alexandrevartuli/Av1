/*
Função:
RealTimeScheduler implementa as equações de escalonamento de tempo real. Cálculo de R_i
(tempo de resposta máximo), utilização do processador U, aplicação da função teto e verificação se R_i <= D_i.

Como se integra ao código externo:
Dentro do RTTaskManager, RealTimeScheduler é chamado para avaliar se as tarefas são escalonáveis.
 Assim, RaceView apenas chama rtManager.areAllSchedulable() e internamente o RTTaskManager usa o RealTimeScheduler para aplicar as equações.

 */


package com.oficial.rtlib;

import java.util.List;
import static java.lang.Math.ceil;

public class RealTimeScheduler {

    public static int ceilInt(double x) {
        return (int)Math.ceil(x);
    }

    public static int calculateResponseTime(TaskInfo ti, List<TaskInfo> allTasks) {
        List<TaskInfo> higherPriorityTasks = allTasks.stream()
                .filter(t -> t.priority < ti.priority)
                .toList();

        int R = 0;
        int oldR;
        do {
            oldR = R;
            int W = 0;
            for (TaskInfo tj : higherPriorityTasks) {
                int numerator = (R + tj.J);
                int nj = ceilInt((double)numerator / tj.P);
                W += nj * tj.C;
            }
            R = ti.C + ti.J + W;
        } while (R != oldR && R < 100000);

        return R;
    }

    public static double calculateUtilization(List<TaskInfo> tasks) {
        double U = 0.0;
        for (TaskInfo t : tasks) {
            U += (double)t.C / t.P;
        }
        return U;
    }

    public static boolean isTaskSchedulable(TaskInfo ti, List<TaskInfo> allTasks) {
        int Ri = calculateResponseTime(ti, allTasks);
        return Ri <= ti.D;
    }
}
