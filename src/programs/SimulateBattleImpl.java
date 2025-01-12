package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Класс для симуляции пошагового сражения между армиями игрока и компьютера.
 * Алгоритм:
 * 1) На каждом раунде собираем все живые юниты и сортируем их по убыванию baseAttack.
 * 2) Ходят по очереди – от самых сильных к самым слабым.
 * 3) Если цель атаки погибает до своего хода, она больше не атакует (исключается из очереди).
 * 4) Если все юниты одной армии погибли, вторая армия завершает ход в текущем раунде.
 * 5) Раунды повторяются, пока обе армии содержат живых юнитов.
 *
 * Сложность алгоритма: O(n^2 log n), где n – общее количество юнитов.
 */
public class SimulateBattleImpl implements SimulateBattle {
   private PrintBattleLog printBattleLog;

   //Симулирует сражение между армией игрока и армией компьютера.
   //playerArmy армия игрока
   //computerArmy армия компьютера
   //throws InterruptedException если симуляция прерывается

   @Override
   public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
      // Пока и у игрока, и у компьютера есть живые юниты — идут раунды
      while (hasLivingUnits(playerArmy) && hasLivingUnits(computerArmy)) {

         // 1) Собираем всех живых юнитов в один список
         List<Unit> allUnits = gatherLivingUnits(playerArmy, computerArmy);

         // 2) Сортируем по убыванию baseAttack (чтобы ходили самые сильные юниты первыми)
         allUnits.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());

         // 3) Проходим по списку от сильнейшего к слабейшему
         for (int i = 0; i < allUnits.size(); i++) {
            Unit attacker = allUnits.get(i);

            // Если атакующий мёртв — пропускаем
            if (!attacker.isAlive()) {
               continue;
            }

            // Атакуем (target может быть null, если цель не найдена)
            Unit target = attacker.getProgram().attack();

            // Логируем атаку, если есть цель
            if (target != null && printBattleLog != null) {
               printBattleLog.printBattleLog(attacker, target);
            }
            // Если в результате атаки погибла цель, она не ходит в этом раунде
            // Если погиб атакующий, он сам не продолжает ход
            // Удалять из списка не обязательно — достаточно проверять isAlive при атаке.
         }
         // Раунд завершён — цикл while проверит, остались ли живые юниты в обеих армиях
      }
   }

   //Собирает всех живых юнитов из армии игрока и компьютера в список.
   //playerArmy армия игрока
   //computerArmy армия компьютера
   //return список всех живых юнитов

   private List<Unit> gatherLivingUnits(Army playerArmy, Army computerArmy) {
      List<Unit> livingUnits = new ArrayList<>();
      for (Unit u : playerArmy.getUnits()) {
         if (u.isAlive()) {
            livingUnits.add(u);
         }
      }
      for (Unit u : computerArmy.getUnits()) {
         if (u.isAlive()) {
            livingUnits.add(u);
         }
      }
      return livingUnits;
   }


    //Проверяет, есть ли в армии живые юниты.
    //return true, если в армии остались живые юниты

   private boolean hasLivingUnits(Army army) {
      for (Unit u : army.getUnits()) {
         if (u.isAlive()) {
            return true;
         }
      }
      return false;
   }

   //Устанавливает объект для вывода лога.
   //printBattleLog объект-логгер

   public void setPrintBattleLog(PrintBattleLog printBattleLog) {
      this.printBattleLog = printBattleLog;
   }
}
