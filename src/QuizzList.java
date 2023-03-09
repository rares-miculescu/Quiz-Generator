package src;

public class QuizzList {

    String name;
    QuestionList[] questions;
    AccountList user;
    QuizzList next;
    int numberOfQuestions;
    int questionIDArray[];

    static int noOfQuizzesTotal = 0;

    public QuizzList() {
        this.name = null;
        this.questions = null;
        this.user = null;
        this.numberOfQuestions = 0;
        this.next = null;
    }

    public QuizzList(String name, QuestionList[] questions, AccountList user, QuizzList next) {
        this.name = name;
        this.questions = questions;
        this.user = user;
        this.numberOfQuestions = questions.length;
        this.next = next;
    }

    public void setName(String name) {
        this.name = name;
    }

    //if the array is null, we create a new array of size 10 and then add the question to the array[index]
    public void setQuestionsEach(QuestionList question, int index) {
        if (this.questions == null) {
            this.questions = new QuestionList[10];
        }
        this.questions[index] = question;
    }

    public void setUser(AccountList user) {
        this.user = user;
    }

    public void setNext(QuizzList next) {
        this.next = next;
    }

    public void setQuestionIDArrayEach(int questionIDArray, int index) {
        if (this.questionIDArray == null) {
            this.questionIDArray = new int[10];
        }
        this.questionIDArray[index] = questionIDArray;
    }

    public String toString() {
        String out = "";
        out += this.user.username + "," + this.user.password + ",";
        out += this.name + ",";
        for (int i = 0; i < this.numberOfQuestions; i++) {
            out += this.questionIDArray[i];
            if (i != this.numberOfQuestions - 1) {
                out += ";";
            }
        }
        return out;
    }

    //checks if the quiz is already in the list
    public boolean findQuizz(String name) {
        if (this.name.equals(name)) {
            return true;
        }
        if (this.next != null) {
            return this.next.findQuizz(name);
        }
        return false;
    }

    //searches the name of the quiz in the list and returns the quiz
    public int getQuizzByName(String name, int id) {
        if (this.name.equals(name)) {
            return id;
        }
        if (this.next != null) {
            return this.next.getQuizzByName(name, id + 1);
        }
        return -1;
    }

    //prints the quiz
    public void printQuizz(QuizzList quizz) {

        for (int i = 0; i < quizz.numberOfQuestions; i++) {
            System.out.print("{\"question-name\":\"" + quizz.questions[i].question + "\", \"question_index\":\"" + quizz.questionIDArray[i]
                    + "\", ");
            if (quizz.questions[i].single) {
                System.out.print("\"question_type\":\"single\", ");
            } else {
                System.out.print("\"question_type\":\"multiple\", ");
            }
            System.out.print("\"answers\":\"[");
            for (int j = 0; j < quizz.questions[i].numberOfAnswers; j++) {
                System.out.print("{\"answer_name\":\"" + quizz.questions[i].answer[j] + "\", \"answer_id\":\"" + quizz.questions[i].answerID[j] + "\"}");
                if (j != quizz.questions[i].numberOfAnswers - 1) {
                    System.out.print(", ");
                }
            }
            System.out.print("]\"}");
            if (i != quizz.numberOfQuestions - 1) {
                System.out.print(", ");
            }

        }
    }

    //searches quiz and prints when found
    public void getQuizzByID(int id, int current) {

        if (id == current) {
            printQuizz(this);
            return;
        }
        if (this.next != null) {
            this.next.getQuizzByID(id, current + 1);
        }

    }

    //searches quiz by id and returns true if found
    public boolean findQuizByID(int id, int current) {
        if (id == current) {
            return true;
        }
        if (this.next != null) {
            return this.next.findQuizByID(id, current + 1);
        }
        return false;
    }

    //searches quiz by id and returns the quiz
    public QuizzList getQuizzByIDReturn(int id, int current) {

        if (id == current) {
            return this;
        }
        if (this.next != null) {
            return this.next.getQuizzByIDReturn(id, current + 1);
        }
        return null;
    }

    //gets the quiz by id and compares its user with the user that is logged in
    //if the user is the same, returns true
    public boolean checkIfUserHasQuiz(String user, int id) {

        QuizzList q = this.getQuizzByIDReturn(id, 1);
        if (q.user.username.equals(user)) {
            return true;
        }
        return false;

    }

    //method gets the answer and calculates the score
    public double checkAnswer(int idAns, QuestionList firstQuestion, double score, double percentage) {

        double individualScore = 0;

        for(int i = 0; i < this.numberOfQuestions; i++){
            //we get each question to access the answers and the correct answer
            QuestionList q = firstQuestion.retQByID(this.questionIDArray[i], 1);

            double scorePerCorrectAnswer = 0;
            double scorePerIncorrectAnswer = 0;

            //we calculate the score for each correct answer and each incorrect answer
            if(q.single){
                scorePerCorrectAnswer = 100;
                scorePerIncorrectAnswer = 100;
            }
            else{
                scorePerCorrectAnswer = 100 / q.correctAns();
                scorePerIncorrectAnswer = 100 / q.incorrectAns();
            }
            //we check if the answer is correct or incorrect
            //if found is -1 it means the id was not in that question
            int found = q.checkAnsCorrect(idAns);
            int done = -1;

            //if found is not -1, it means the id was in that question and we update the score and move on
            switch(found){
                case 0:
                    individualScore -= scorePerIncorrectAnswer;
                    done = 1;
                    break;
                case 1:
                    individualScore += scorePerCorrectAnswer;
                    done = 1;
                    break;
            }

            if(done == 1){
                break;
            }

        }
        //we calculate the score for the whole quiz
        score += individualScore * percentage / 100;
        return score;

    }

    //recursive method to print the list
    public void printAllQuizzes(int index, AccountList user) {

        System.out.print("{\"quizz_id\" : \"" + index + "\", \"quizz_name\" : \"" +
                this.name + "\", \"is_completed\" : \"");
        AccountList acc = user.retUsr(this.user.username);
        if(acc.checkIfQuizCompleted(index)) {
            System.out.print("True");
        }
        else{
                System.out.print("False");
        }
        System.out.print("\"}");

        if(this.next != null){
            System.out.print(", ");
            this.next.printAllQuizzes(index + 1, user);
        }

    }

    //method to delete a quiz
    //when found we set the previous node to point to the next node
    public void deleteQuiz(int id, int currentID){
        if(this.next != null){
            if(currentID == id - 1){
                this.next = this.next.next;
                QuizzList.noOfQuizzesTotal--;
            }
            else{
                this.next.deleteQuiz(id, currentID + 1);
            }
        }
    }

    public String getQuizName(int id, int currentID){
        if(currentID == id){
            return this.name;
        }
        else{
            return this.next.getQuizName(id, currentID + 1);
        }
    }

}