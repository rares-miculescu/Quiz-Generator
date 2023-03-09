package src;

public class QuestionList {

    AccountList user;
    String question;
    String[] answer;

    int[] answerID;
    boolean single;
    boolean[] correct;
    QuestionList next;
    int numberOfAnswers;

    static int noOfAnsersTotal = 0;

    public QuestionList(AccountList user, String question, String[] answer, boolean single, boolean[] correct, int[] answerID) {
        this.user = user;
        this.question = question;
        this.answer = answer;
        this.single = single;
        this.correct = correct;
        this.numberOfAnswers = answer.length;
        this.answerID = answerID;
        this.next = null;
    }

    public QuestionList(){
        this.user = null;
        this.question = null;
        this.answer = null;
        this.single = false;
        this.correct = null;
        this.numberOfAnswers = 0;
        this.answerID = null;
        this.next = null;
    }

    //doing the same as setAnswer
    public void setAnswerIDEach(int answerID, int index) {
        if(this.answerID == null){
            this.answerID = new int[5];
        }
        this.answerID[index] = answerID;
    }

    public void setNext(QuestionList next) {
        this.next = next;
    }

    public void setUser(AccountList user) {
        this.user = user;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    //if the array is null, we create a new array of size 5 and then add the answer to the array[index]
    public void setAnswer(String answer, int index) {
        if(this.answer == null){
            this.answer = new String[5];
        }
        this.answer[index] = answer;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    //same as setAnswer
    public void setCorrect(boolean correct, int index) {
        if(this.correct == null){
            this.correct = new boolean[5];
        }
        this.correct[index] = correct;
    }

    public void setNumberOfAnswers(int numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    //checks if the question name already exists in the list
    public boolean findQuestion(String question) {
        if(this.question.equals(question)) {
            return true;
        }
        if(this.next != null) {
            return this.next.findQuestion(question);
        }
        return false;
    }

    @Override
    public String toString() {
        String answerString = "";
        String correctString = "";
        for(int i = 0; i < numberOfAnswers; i++){
            answerString += this.answer[i];
            if(i != numberOfAnswers - 1){
                answerString += ";";
            }
        }
        for(int i = 0; i < numberOfAnswers; i++){
            if(this.correct[i]){
                correctString += "1";
            }else{
                correctString += "0";
            }
            if(i != numberOfAnswers - 1){
                correctString += ";";
            }

        }
        int singleInt = 0;
        if(this.single){
            singleInt = 1;
        }
        return this.user.username + "," + this.user.password + "," + this.question + "," + singleInt + "," + answerString + "," + correctString;
    }

    //checks if the numbers of correct answers is correct
    //counts the number of correct answers and checks the type of question, if the two match returns true
    public boolean checkCorrect(){
        int correctCount = 0;
        for(int i = 0; i < numberOfAnswers; i++){
            if(this.correct[i]){
                correctCount++;
            }
        }
        if(single) {
            if (correctCount == 1) {
                return true;
            }
        }
        else{
            if(correctCount > 1){
                return true;
            }
        }

        return false;
    }

    //checks if there are two answers with the same name
    public boolean checkDuplicates(){
        for(int i = 0; i < numberOfAnswers - 1; i++){
            for(int j = i + 1; j < numberOfAnswers; j++){
                if(answer[i].equals(answer[j]))
                    return false;
            }
        }
        return true;

    }

    //we search for the id of the question with the title q
    public int getQuestionID(String q, int id){
        if(this.question.equals(q)){
            return id;
        }
        if(this.next != null){
            return this.next.getQuestionID(q, id + 1);
        }
        return -1;
    }

    //recursive function of printing the questions
    public void printQuestion(int id){

        System.out.print("{\"question_id\" : \"" + id + "\", \"question_name\" : \"" + this.question + "\"}");
        if(this.next != null){
            System.out.print(", ");
            this.next.printQuestion(id + 1);
        }

    }

    //returns the question by giving the id (the second parameter is the id of the currwnt question)
    //rather than storing it, i thought it would be better to give it as a parameter
    public QuestionList getQuestion(int id, int currentID){
        if(id == currentID){
            return this;
        }
        if(this.next != null){
            return this.next.getQuestion(id, currentID + 1);
        }
        return null;
    }

    public QuestionList retQByID(int id, int currentID){
        if(id == currentID){
            return this;
        }
        if(this.next != null){
            return this.next.retQByID(id, currentID + 1);
        }
        return null;
    }

    //goes through the array of answerID and checks if the id matches with the one given as a parameter
    //if it does, checks if it is correct
    //if it is correct returns 1, if it is false returns 0
    //if the method gets to the end, it means that the id is not in the array, so it returns -1
    public int checkAnsCorrect(int ansID){

        boolean found = false;
        for(int i = 0; i < this.numberOfAnswers; i++){
            if(this.answerID[i] == ansID){
                found = true;
                if(this.correct[i]){
                    return 1;
                }
                else{
                    return 0;
                }
            }
        }
        if(!found){
            return -1;
        }
        //i had to make a return here because the compiler was complaining
        return -1;
    }

    //calculates the number of correct answers
    public int correctAns(){
        int correctAns = 0;
        for(int i = 0; i < this.numberOfAnswers; i++){
            if(this.correct[i]){
                correctAns++;
            }
        }
        return correctAns;
    }

    //calculates the number of incorrect answers
    public int incorrectAns(){
        int incorrectAns = 0;
        for(int i = 0; i < this.numberOfAnswers; i++){
            if(!this.correct[i]){
                incorrectAns++;
            }
        }
        return incorrectAns;
    }
}