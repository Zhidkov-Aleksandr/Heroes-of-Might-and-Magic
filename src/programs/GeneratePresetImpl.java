package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        Army computerArmy = new Army();
        List<Unit> selectedUnits = new ArrayList<>();
        Map<String, Integer> unitTypeCount = new HashMap<>();
        Map<String, Integer> unitOrderCount = new HashMap<>(); // Для порядковых номеров
        Random random = new Random();
        int n = unitList.size();
        int[][] dp = new int[n + 1][maxPoints + 1];

        // Построение таблицы для алгоритма динамического программирования
        for (int i = 1; i <= n; i++) {
            Unit unit = unitList.get(i - 1);
            for (int j = 1; j <= maxPoints; j++) {
                if (unit.getCost() <= j) {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i - 1][j - unit.getCost()] + unit.getBaseAttack() + unit.getHealth());
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }

        // Выбор юнитов для включения в армию, начиная с максимального количества очков
        int remainingPoints = maxPoints;
        for (int i = n; i > 0 && remainingPoints > 0; i--) {
            if (dp[i][remainingPoints] != dp[i - 1][remainingPoints]) {
                Unit unit = unitList.get(i - 1);
                int maxUnitsForType = Math.min(11, remainingPoints / unit.getCost()); // Не более 11 юнитов одного типа
                int countOfType = unitTypeCount.getOrDefault(unit.getUnitType(), 0);

                while (countOfType < maxUnitsForType && remainingPoints >= unit.getCost()) {
                    int[] coords = findAvailableCoordinates(selectedUnits, random, 0);
                    if (coords == null) {
                        System.out.println("Не удалось добавить юнита из-за недоступных координат.");
                        break;
                    }

                    // Увеличиваем порядковый номер для данного типа юнита
                    int order = unitOrderCount.getOrDefault(unit.getUnitType(), 0) + 1;
                    unitOrderCount.put(unit.getUnitType(), order);

                    // Создаём новое имя с порядковым номером
                    String newName = unit.getUnitType() + " " + order;

                    Unit newUnit = new Unit(
                            newName,                        // Имя с порядковым номером
                            unit.getUnitType(),
                            unit.getHealth(),
                            unit.getBaseAttack(),
                            unit.getCost(),
                            unit.getAttackType(),
                            unit.getAttackBonuses(),
                            unit.getDefenceBonuses(),
                            coords[0],
                            coords[1]
                    );

                    selectedUnits.add(newUnit);
                    countOfType++;
                    unitTypeCount.put(unit.getUnitType(), countOfType);
                    remainingPoints -= unit.getCost();
                }
            }
        }

        int usedPoints = maxPoints - remainingPoints;
        computerArmy.setUnits(selectedUnits);
        computerArmy.setPoints(usedPoints);

        System.out.println("Generated Army:");
        System.out.println("Used points: " + usedPoints);
        System.out.println("Units: " + selectedUnits.size());
        for (Unit u : computerArmy.getUnits()) {
            System.out.println("Unit: " + u.getName()
                    + " | Type: " + u.getUnitType()
                    + " | Cost: " + u.getCost()
                    + " | Coordinates: (" + u.getxCoordinate() + ", " + u.getyCoordinate() + ")");
        }

        return computerArmy;
    }

    private int[] findAvailableCoordinates(List<Unit> existingUnits, Random rand, int attempt) {
        if (attempt >= 100) {
            System.out.println("Failed to find coordinates after 100 attempts.");
            return null;
        }

        int x = rand.nextInt(3); // X: 0..2
        int y = rand.nextInt(21);  // Y: 0..20

        boolean occupied = existingUnits.stream()
                .anyMatch(u -> u.getxCoordinate() == x && u.getyCoordinate() == y);
        if (!occupied) {
            return new int[]{x, y};
        }

        return findAvailableCoordinates(existingUnits, rand, attempt + 1);
    }
}
