package src;

public class AccountList {
    String username;
    String password;
    AccountList next;

    String score;

    public AccountList(){
        this.username = null;
        this.password = null;
        this.next = null;
        this.score = "-1";
    }

    public AccountList(String username, String password, String score) {
        this.username = username;
        this.password = password;
        this.next = null;
        this.score = score;
    }

    public AccountList(String username, String password) {
        this.username = username;
        this.password = password;
        this.next = null;
        this.score = "-1";
    }

    public void setNext(AccountList next) {
        this.next = next;
    }
    public boolean findAccount(String username) {
        if(this.username.equals(username)) {
            return true;
        }
        if(this.next != null) {
            return this.next.findAccount(username);
        }
        return false;
    }

    //we search through users to see if the credentials given are found in the list
    public boolean checkPassword(AccountList account) {

        if(this.username.equals(account.username) && this.password.equals(account.password)) {
            return true;
        }
        if(this.next != null) {
            return this.next.checkPassword(account);
        }
        return false;
    }

    @Override
    public String toString() {
        return this.username + "," + this.password + "," + this.score;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public AccountList retUsr(String name){
        if(this.username.equals(name)){
            return this;
        }
        if(this.next != null){
            return this.next.retUsr(name);
        }
        return null;
    }

    //we get the user and check the score for the current quiz
    //if it is -1 returns false
    public static boolean checkIfUserCompletedQuiz(String user, int id, AccountList first){

        AccountList temp = first.retUsr(user);

        if(temp.score.equals("-1")){
            return false;
        }
        if(temp.score.split(";")[id].equals("-1"))
            return false;
        return true;
    }

    //first we check if the score is -1
    //if it is we initialize the score (for each quiz with -1)
    //then we update the score for the current quiz
    public static void addScore(int qNum, String user, int index, double score, AccountList first){
        String temp = "-1;";
        AccountList tempAcc = first.retUsr(user);
        if(tempAcc.score.equals("-1")){
            tempAcc.score += ";";
            for(int i = 1; i <= qNum; i++) {
                tempAcc.score += "-1";
                if (i != qNum) {
                    tempAcc.score += ";";
                }
            }
        }

        for(int i = 1; i < tempAcc.score.split(";").length; i++){
            if(tempAcc.score.split(";")[i].equals("-1")){
                if(i == index){
                    temp += String.valueOf((int)score);
                }
                else{
                    temp += "-1";
                }
            }
            else{
                temp += tempAcc.score.split(";")[i];
            }
            if(i != tempAcc.score.split(";").length - 1){
                temp += ";";
            }
        }
        tempAcc.score = temp;
    }

    public boolean checkIfQuizCompleted(int id){
        if(this.score.equals("-1")){
            return false;
        }
        if(this.score.split(";")[id].equals("-1"))
            return false;
        else return true;

    }

    //printing the quiz name and score for each quiz
    public void printSolutions(QuizzList first){
        for(int i = 1; i < this.score.split(";").length; i++){
            if(this.score.split(";")[i].equals("-1")){
               continue;
            }
            else{
                String quizName = first.getQuizName(i, 1);
                System.out.print("{\"quiz-id\" : \"" + i + "\", \"quiz-name\" : \"" + quizName + "\", \"score\" : \"" +
                        score.split(";")[i] + "\", \"index_in_list\" : \"" + i + "\"}");
            }
        }
    }

}
