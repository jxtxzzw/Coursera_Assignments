import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseballElimination {

    private static class Team {
        int wins;
        int loses;
        int remaining;

        public Team(int wins, int loses, int remaining) {
            this.wins = wins;
            this.loses = loses;
            this.remaining = remaining;
        }
    }

    private final int numberOfTeams;
    private final HashMap<String, Team> teams = new HashMap<>();
    private final HashMap<String, Integer> teamNameToTeamIndex = new HashMap<>();
    private final HashMap<Integer, String> teamIndexToTeamName = new HashMap<>();
    private final int[][] remainingAgainst;
    private int flow;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        numberOfTeams = in.readInt();
        remainingAgainst = new int[numberOfTeams][numberOfTeams];
        in.readLine();
        // 依次处理每一行，即每一支球队
        for (int i = 0; i < numberOfTeams; i++) {
            String name = in.readString();
            int wins = in.readInt();
            int loses = in.readInt();
            int remaining = in.readInt();
            // 存入 teams，并建立 id 与 name 的互相映射
            teams.put(name, new Team(wins, loses, remaining));
            teamIndexToTeamName.put(i, name);
            teamNameToTeamIndex.put(name, i);
            // 在剩余的比赛场次中，与其他各队伍的比赛次数
            for (int j = 0; j < numberOfTeams; j++) {
                remainingAgainst[i][j] = in.readInt();
            }
            in.readLine();
        }
    }

    // number of teams
    public int numberOfTeams() {
        return numberOfTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return teams.keySet();
    }

    // The last six methods should throw an IllegalArgumentException if one (or both) of the input arguments are invalid teams.
    private void isValid(String team) {
        if (!teams.containsKey(team)) {
            throw new IllegalArgumentException();
        }
    }

    // number of wins for given team
    public int wins(String team) {
        isValid(team);
        return teams.get(team).wins;
    }

    // number of losses for given team
    public int losses(String team) {
        isValid(team);
        return teams.get(team).loses;
    }

    // number of remaining games for given team
    public int remaining(String team) {
        isValid(team);
        return teams.get(team).remaining;
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        isValid(team1);
        isValid(team2);
        int i = teamNameToTeamIndex.get(team1);
        int j = teamNameToTeamIndex.get(team2);
        return remainingAgainst[i][j];
    }

    private FordFulkerson getMaxFlow(String team) {
        // 待评估的球队 x 的编号
        int x = teamNameToTeamIndex.get(team);
        // 计算顶点个数
        int numberOfGameVertices = numberOfTeams * (numberOfTeams - 1) / 2;
        int numberOfVertices = numberOfGameVertices + numberOfTeams + 2; // 加上起点和终点
        // 构建网络流
        FlowNetwork flowNetwork = new FlowNetwork(numberOfVertices);
        int s = 0;
        int t = numberOfVertices - 1;
        int index = 1;
        flow = 0;
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == x) {
                continue;
            }
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (j == x) {
                    continue;
                }
                // 首先将起点 s 与所有可能发生的比赛（game vertices）连接
                // 起点是 s，game vertices 的编号从 1 开始，限制容量为 remainingAgainst[i][j]
                flowNetwork.addEdge(new FlowEdge(s, index, remainingAgainst[i][j]));
                // 然后将 team vertices 与 game vertices 连接，容量为无穷大
                // 比赛涉及到的双方是 i 和 j，因此代表它们的 team vertices 就是 i(j) + numberOfGameVertices + 1
                flowNetwork.addEdge(new FlowEdge(index, i + numberOfGameVertices + 1, Double.POSITIVE_INFINITY));
                flowNetwork.addEdge(new FlowEdge(index, j + numberOfGameVertices + 1, Double.POSITIVE_INFINITY));
                index++;
                // 累加来自起点的所有流量
                flow += remainingAgainst[i][j];
            }
            // 最后，我们需要将 team vertices 与终点 t 连接，容量限制为 w[x] + r[x] - w[i]
            int wX = teams.get(team).wins;
            int rX = teams.get(team).remaining;
            int wI = teams.get(teamIndexToTeamName.get(i)).wins;
            // 容量必须是非负数，所以如果这里相减得到负数，则意味着可以提前结束判断，认为 x 已经被淘汰
            // 注意如果将 capacity 设置为 Math.max(0, wX + rX - wI) 会导致有些被 eliminate 的球队无法被正确淘汰
            if (wX + rX - wI < 0) {
                return null;
            } else {
                flowNetwork.addEdge(new FlowEdge(i + numberOfGameVertices + 1, t, wX + rX - wI));
            }
        }

        return new FordFulkerson(flowNetwork, s, t);
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        isValid(team);
        FordFulkerson maxFlow = getMaxFlow(team);
        if (maxFlow == null) {
            return true;
        } else {
            // 球队 x 没有被淘汰，当且仅当最大流的值大于等于从 s 出发的所有流量
            return flow > maxFlow.value();
        }
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        isValid(team);
        if (!isEliminated(team)) {
            return null;
        }
        ArrayList<String> list = new ArrayList<>();
        int x = teamNameToTeamIndex.get(team);
        int numberOfGameVertices = numberOfTeams * (numberOfTeams - 1) / 2;
        FordFulkerson maxFlow = getMaxFlow(team);
        for (int index = 0; index < numberOfTeams; index++) {
            if (index == x) {
                continue;
            }
            if (maxFlow == null) {
                int wX = teams.get(team).wins;
                int rX = teams.get(team).remaining;
                int wI = teams.get(teamIndexToTeamName.get(index)).wins;
                if (wX + rX - wI < 0) {
                    list.add(teamIndexToTeamName.get(index));
                }
            } else if (maxFlow.inCut(index + numberOfGameVertices + 1)) {
                list.add(teamIndexToTeamName.get(index));
            }
        }
        return list;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

}
