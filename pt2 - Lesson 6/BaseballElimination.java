/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;

public class BaseballElimination {

    private final int teamNumber;
    private final ArrayList<String> teamNames;

    /***
     * index - index of team
     * array contents:
     * 0 - number of wins
     * 1 - number of losses
     * 2 - number of remaining games
     */
    private final int[][] teamGeneralData;
    /***
     * index - index of team
     * Array of remaining games against each other
     */
    private final int[][] teamGamesData;

    private int curMaxFlow;

    public BaseballElimination(String filename)
    // create a baseball division from given filename in format specified below
    {
        int datanum = 3;

        In in = new In(filename);
        teamNumber = in.readInt();
        in.readLine();

        teamNames = new ArrayList<String>(teamNumber);

        teamGeneralData = new int[teamNumber][datanum];
        teamGamesData = new int[teamNumber][teamNumber];


        for (int i = 0; i < teamNumber; ++i) {

            String s = in.readLine();
            if (s != null) {
                String[] parts = s.trim().split("\\s+");

                teamNames.add(parts[0]);

                for (int j = 0; j < 3; j++) {
                    teamGeneralData[i][j] = Integer.parseInt(parts[j + 1]);
                }

                // Last 5 numbers: games data
                for (int j = 0; j < teamNumber; j++) {
                    teamGamesData[i][j] = Integer.parseInt(parts[j + datanum + 1]);
                }
            }

        }


    }

    private int getTeamIndex(String team) {
        for (int i = 0; i < teamNumber; ++i) {
            if (teamNames.get(i).equals(team)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid team name");
    }

    public int numberOfTeams()
    // number of teams
    {
        return teamNumber;
    }

    public Iterable<String> teams()
    // all teams
    {
        return teamNames;
    }

    public int wins(String team)
    // number of wins for given team
    {
        int i = getTeamIndex(team);
        return teamGeneralData[i][0];
    }

    public int losses(String team)
    // number of losses for given team
    {
        int i = getTeamIndex(team);
        return teamGeneralData[i][1];
    }

    public int remaining(String team)
    // number of remaining games for given team
    {
        int i = getTeamIndex(team);
        return teamGeneralData[i][2];
    }

    public int against(String team1, String team2)
    // number of remaining games between team1 and team2
    {
        int i = getTeamIndex(team1);
        int j = getTeamIndex(team2);
        return teamGamesData[i][j];
    }

    private int triviallyEliminatedBy(int i) {
        int maxPossibleWins = teamGeneralData[i][0] + teamGeneralData[i][2];
        for (int j = 0; j < teamNumber; ++j) {
            if (i == j) continue;
            if (maxPossibleWins < teamGeneralData[j][0]) return j;
        }
        return -1;
    }

    private FlowNetwork generateFlowNetwork(int idx) {

        int v = (teamNumber) * (teamNumber - 1) / 2 + teamNumber + 2;
        FlowNetwork flowNetwork = new FlowNetwork(v);

        curMaxFlow = 0;
        /***
         * 0 - s
         * 1..(teamNumber-1)(teamNumber)/2 - pairs
         * (VNumber-1-teamNumber)..(VNumber-2) - teams
         * (VNumber-1) - t
         */
        int gij = 0;
        for (int i = 0; i < teamNumber; ++i) {
            for (int j = i + 1; j < teamNumber; ++j) {
                gij++;
                if (i == idx || j == idx) continue;
                flowNetwork.addEdge(new FlowEdge(0, gij, (double) teamGamesData[i][j]));
                curMaxFlow += teamGamesData[i][j];

                flowNetwork.addEdge(
                        new FlowEdge(gij, v - 1 - teamNumber + i, Double.POSITIVE_INFINITY));
                flowNetwork.addEdge(
                        new FlowEdge(gij, v - 1 - teamNumber + j, Double.POSITIVE_INFINITY));

            }
            int maxWinCapacity = teamGeneralData[idx][0] + teamGeneralData[idx][2]
                    - teamGeneralData[i][0];
            flowNetwork.addEdge(new FlowEdge(v - 1 - teamNumber + i, v - 1, maxWinCapacity));

        }
        return flowNetwork;
    }

    public boolean isEliminated(String team)
    // is given team eliminated?
    {
        int idx = getTeamIndex(team);
        if (triviallyEliminatedBy(idx) != -1) return true;

        FlowNetwork flowNetwork = generateFlowNetwork(idx);
        FordFulkerson ff = new FordFulkerson(flowNetwork, 0, flowNetwork.V() - 1);
        double maxFlow = ff.value();
        return (maxFlow < curMaxFlow);
    }

    public Iterable<String> certificateOfElimination(String team)
    // subset R of teams that eliminates given team; null if not eliminated
    {
        int idx = getTeamIndex(team);
        int elim = triviallyEliminatedBy(idx);
        if (elim != -1) return List.of(teamNames.get(elim));

        FlowNetwork flowNetwork = generateFlowNetwork(idx);
        FordFulkerson ff = new FordFulkerson(flowNetwork, 0, flowNetwork.V() - 1);

        ArrayList<String> result = new ArrayList<>();

        for (int i = 0; i < teamNumber; ++i) {
            if (i == idx) continue;
            if (ff.inCut(flowNetwork.V() - 1 - teamNumber + i)) {
                result.add(teamNames.get(i));
            }
        }
        return result.isEmpty() ? null : result;
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
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }

    }
}
