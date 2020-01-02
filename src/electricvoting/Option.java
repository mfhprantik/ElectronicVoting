/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electricvoting;

/**
 *
 * @author Prantik
 */
public class Option {

    private int id;
    private String name;
    private int votes;
    private boolean voted;

    private Option() {
    }

    public Option(int id, String name, int votes) {
        this.id = id;
        this.name = name;
        this.votes = votes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVotes() {
        return votes;
    }

    public boolean isVoted() {
        return voted;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    public double getVotePercentage(int totalVotes) {
        if (totalVotes == 0) {
            return 0;
        }
        return (votes * 100.0) / totalVotes;
    }
}
