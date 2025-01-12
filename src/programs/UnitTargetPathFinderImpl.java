package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
   private static final int WIDTH = 27;
   private static final int HEIGHT = 21;

   @Override
   public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
      // Начальные координаты атакующего юнита
      int startX = attackUnit.getxCoordinate();
      int startY = attackUnit.getyCoordinate();
      // Целевые координаты атакуемого юнита
      int targetX = targetUnit.getxCoordinate();
      int targetY = targetUnit.getyCoordinate();

      // Если уже в одной клетке
      if (startX == targetX && startY == targetY) {
         return Collections.singletonList(new Edge(startX, startY));
      }

      // Формируем карту занятых клеток
      boolean[][] blocked = new boolean[HEIGHT][WIDTH];
      for (Unit u : existingUnitList) {
         if (u.isAlive()) {
            int ux = u.getxCoordinate();
            int uy = u.getyCoordinate();
            // Разрешаем заход на клетку цели и стартовую клетку
            if (!(ux == targetX && uy == targetY) && !(ux == startX && uy == startY)) {
               if (uy >= 0 && uy < HEIGHT && ux >= 0 && ux < WIDTH) {
                  blocked[uy][ux] = true;
               }
            }
         }
      }

      // Используем приоритетную очередь для алгоритма Дейкстры
      PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.cost));
      pq.offer(new Node(startX, startY, 0));

      // Массив для отслеживания минимальной стоимости пути до каждой клетки
      int[][] minCost = new int[HEIGHT][WIDTH];
      for (int[] row : minCost) {
         Arrays.fill(row, Integer.MAX_VALUE);
      }
      minCost[startY][startX] = 0;

      // Массив для отслеживания пути (предыдущей клетки)
      Map<String, String> parent = new HashMap<>();

      // Направления движения: вправо, влево, вниз, вверх
      int[][] dirs = {
              {1, 0},  // вправо
              {-1, 0}, // влево
              {0, 1},  // вниз
              {0, -1}  // вверх
      };

      while (!pq.isEmpty()) {
         Node current = pq.poll();
         int cx = current.x;
         int cy = current.y;
         int currentCost = current.cost;

         // Если достигли целевой клетки, прерываем цикл
         if (cx == targetX && cy == targetY) {
            break;
         }

         for (int[] d : dirs) {
            int nx = cx + d[0];
            int ny = cy + d[1];

            // Проверка границ поля
            if (nx < 0 || nx >= WIDTH || ny < 0 || ny >= HEIGHT) {
               continue;
            }

            // Проверка, что клетка не заблокирована
            if (blocked[ny][nx]) {
               continue;
            }

            int newCost = currentCost + 1; // Стоимость шага (каждый шаг равен 1)

            // Если найден более короткий путь к этой клетке
            if (newCost < minCost[ny][nx]) {
               minCost[ny][nx] = newCost;
               pq.offer(new Node(nx, ny, newCost));
               parent.put(nx + "," + ny, cx + "," + cy);
            }
         }
      }

      // Если не нашли путь до целевой клетки, возвращаем пустой список
      if (!parent.containsKey(targetX + "," + targetY)) {
         return Collections.emptyList();
      }

      // Восстанавливаем путь
      List<Edge> path = new LinkedList<>();
      String cur = targetX + "," + targetY;
      while (cur != null) {
         String[] parts = cur.split(",");
         int x = Integer.parseInt(parts[0]);
         int y = Integer.parseInt(parts[1]);
         path.add(0, new Edge(x, y)); // Добавляем в начало списка
         cur = parent.get(cur);
      }

      return path;
   }

   //Вспомогательный класс для узла с координатами и стоимостью.

   private static class Node {
      int x, y, cost;

      Node(int x, int y, int cost) {
         this.x = x;
         this.y = y;
         this.cost = cost;
      }
   }
}
