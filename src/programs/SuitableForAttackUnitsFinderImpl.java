package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

   @Override
   public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
      // Список для хранения доступных для атаки юнитов
      List<Unit> suitableUnits = new ArrayList<>();

      // Определяем диапазон координат X, где расположена армия противника
      int startX = isLeftArmyTarget ? 0 : Math.max(0, unitsByRow.size() - 3);
      int endX = isLeftArmyTarget ? Math.min(3, unitsByRow.size()) : unitsByRow.size();

      System.out.println("Targeting " + (isLeftArmyTarget ? "left army (computer)" : "right army (player)") + ".");

      // Проходим по каждой строке в указанном диапазоне
      for (int x = startX; x < endX; x++) {
         List<Unit> unitRow = unitsByRow.get(x); // Получаем текущую строку юнитов
         for (int y = 0; y < unitRow.size(); y++) {
            Unit unit = unitRow.get(y);

            // Проверяем, что юнит существует и жив
            if (unit != null && unit.isAlive()) {
               // Проверяем, что юнит не перекрыт
               if (isValidTarget(unitsByRow, x, y, isLeftArmyTarget)) {
                  suitableUnits.add(unit);
                  System.out.println("Suitable target found: Unit at X " + x + ", Y " + y
                          + ", Base Attack: " + unit.getBaseAttack()
                          + ", Health: " + unit.getHealth()
                          + ", Coordinates: (" + unit.getxCoordinate() + ", " + unit.getyCoordinate() + ")");
               } else {
                  System.out.println("Unit at X " + x + ", Y " + y + " is blocked and cannot be targeted.");
               }
            }
         }
      }

      System.out.println("Total suitable targets found: " + suitableUnits.size());
      return suitableUnits;
   }

   private boolean isValidTarget(List<List<Unit>> unitsByRow, int x, int y, boolean isLeftArmyTarget) {
      int step = isLeftArmyTarget ? 1 : -1; // Направление проверки: вправо для левой армии, влево для правой
      int checkY = y + step;

      // Проверяем, что индекс находится в пределах допустимого диапазона
      if (checkY >= 0 && checkY < unitsByRow.get(x).size()) {
         if (unitsByRow.get(x).get(checkY) != null) {
            return false; // Юнит заблокирован с противоположной стороны
         }
      }
      return true; // Юнит открыт для атаки
   }
}
