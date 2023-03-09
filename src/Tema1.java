package src;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public class Tema1 {

    public static void main(final String[] args) {


        //if there are no args we print hello world
        if(args == null){
            System.out.println("Hello world!");
            return;
        }

        AccountList first = null;
        AccountList previous = null;

        //reading the users file and creating the list
        try(BufferedReader br = new BufferedReader(new FileReader("./user.csv"))) {
            String line;
            while((line = br.readLine()) != null && !line.equals(" ")) {

                if(line.equals("")) {
                    break;
                }
                //first parameter is the username, second is password and last is the score
                AccountList account = new AccountList(line.split(",")[0], line.split(",")[1], line.split(",")[2]);
                if(first == null) {
                    first = account;
                }
                if(previous != null) {
                    previous.setNext(account);
                }
                previous = account;
            }

        }
         catch (IOException e) {
            e.printStackTrace();
        }

        QuestionList firstQuestion = null;
        QuestionList previousQuestion = null;

        //reading the questions file and creating the list
        try(BufferedReader br = new BufferedReader(new FileReader("./question.csv"))) {
            String line;
            QuestionList.noOfAnsersTotal = 0;
            while((line = br.readLine()) != null && !line.equals("")) {

                if(line.equals("")) {
                    break;
                }
                QuestionList question = new QuestionList();
                //first two parameters are the user credentials (user and pass)
                AccountList usr = new AccountList(line.split(",")[0], line.split(",")[1]);
                question.user = usr;
                //third parameter is the question title
                question.question = line.split(",")[2];
                //fourth parameter is the question type (stored as 1 or 0)
                String single = line.split(",")[3];
                if(single.equals("1")) {
                    question.single = true;
                }
                else {
                    question.single = false;
                }
                //fifth parameter are the asnwers (separated with ;)
                int j = 0;
                for(; j < line.split(",")[4].split(";").length; j++) {
                    question.setAnswer(line.split(",")[4].split(";")[j], j);
                    //noOfAnswersTotal is used to keep count of the total number of answers
                    QuestionList.noOfAnsersTotal++;
                    question.setAnswerIDEach(QuestionList.noOfAnsersTotal, j);
                }
                question.setNumberOfAnswers(j);
                boolean[] correct = new boolean[question.numberOfAnswers];
                //sixth parameter tells for each answer if it is correct or not (separated with ;)
                for(int i = 0; i < question.numberOfAnswers; i++) {
                    if(line.split(",")[5].split(";")[i].equals("1")) {
                        correct[i] = true;
                    }
                    else {
                        correct[i] = false;
                    }
                }
                question.correct = correct;
                //after creating the question we add it to the list
                if(firstQuestion == null) {
                    firstQuestion = question;
                }
                if(previousQuestion != null) {
                    previousQuestion.setNext(question);
                }
                previousQuestion = question;
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        QuizzList firstQuizz = null;
        QuizzList previousQuizz = null;

        //reading the quizz file and creating the list
        try(BufferedReader br = new BufferedReader(new FileReader("./quizz.csv"))) {

            String line;
            QuizzList.noOfQuizzesTotal = 0;
            while((line = br.readLine()) != null && !line.equals(" ")) {

                if(line.equals("")) {
                    break;
                }
                QuizzList quizz = new QuizzList();
                AccountList usr = new AccountList(line.split(",")[0], line.split(",")[1]);
                //first two parameters are the user credentials (user and pass)
                quizz.setUser(usr);
                //third parameter is the quizz title
                quizz.setName(line.split(",")[2]);
                int i = 0;
                //fourth parameter are the questions ids(separated with ;), that lets us get the questions for the array
                for(; i < line.split(",")[3].split(";").length; i++) {
                    String idStr = line.split(",")[3].split(";")[i];
                    int id = Integer.parseInt(idStr);
                    QuestionList question = firstQuestion.getQuestion(id, 1);
                    quizz.setQuestionsEach(question, i);
                    quizz.setQuestionIDArrayEach(id, i);
                }
                quizz.numberOfQuestions = i;
                //noOfQuizzesTotal is used to store the number of quizzes
                QuizzList.noOfQuizzesTotal++;
                //after creating the quizz we add it to the list
                if(firstQuizz == null) {
                    firstQuizz = quizz;
                }
                if(previousQuizz != null) {
                    previousQuizz.setNext(quizz);
                }
                previousQuizz = quizz;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(args.length == 0) {
            System.out.println("No arguments");
            return;
        }

        if(args[0].equals("-create-user")){

            //checking if we have enaugh parameters to create a user
            if(args.length < 3){
                switch (args.length){
                    case 1:
                        System.out.println("{ 'status' : 'error', 'message' : 'Please provide username'}");
                        break;
                    case 2:
                        if(args[1].split(" ")[0].equals("-u"))
                            System.out.println("{ 'status' : 'error', 'message' : 'Please provide password'}");
                        else
                            System.out.println("{ 'status' : 'error', 'message' : 'Please provide username'}");
                        break;
                }
                return;
            }
            String username = args[1].split(" ")[1].substring(1, args[1].split(" ")[1].length() - 1);
            String password = args[2].split(" ")[1].substring(1, args[2].split(" ")[1].length() - 1);

            //we start to extract the parameters
            if(args[1].split(" ")[0].equals("-u")){
                //checking if the username is already taken
                if (first != null && first.findAccount(username))
                    System.out.println("{ 'status' : 'error', 'message' : 'User already exists' }");
                else {
                    if(args[2].split(" ")[0].equals("-p")){
                        //creating the user
                        AccountList account = new AccountList(username, password);
                        if(first == null){
                            first = account;
                        }
                        if(previous != null){
                            previous.next = account;
                        }
                        previous = account;
                        System.out.println("{ 'status' : 'ok', 'message' : 'User created successfully' }");
                    }
                    else{
                        System.out.println("{ 'status' : 'error', 'message' : 'Please provide password'}");
                    }
                }
            }
            else {
                System.out.println("{ 'status' : 'error', 'message' : 'Please provide username'}");
            }

        }

        if(args[0].equals("-create-question")){

            int i = 0;
            QuestionList question = new QuestionList();
            AccountList acc = new AccountList();

            //checking the login credentials
            String acco = Tema1.authenticate(args, first);
            if(acco == null)
                return;

            if(args.length > 1) {

                if(args.length >= 3){

                    acc.setUsername(args[1].split(" ")[1].substring(1, args[1].split(" ")[1].length() - 1));
                    acc.setPassword(args[2].split(" ")[1].substring(1, args[2].split(" ")[1].length() - 1));
                    question.setUser(acc);
                    //setting the user for the question

                    if(args.length > 5){

                        //setting the question text
                        if(args[3].split(" ")[0].equals("-text")){
                            String txt = args[3].split("'")[1];
                            question.setQuestion(txt);
                        }
                        else{
                            System.out.println("{ 'status' : 'error', 'message' : 'No question text provided'}");
                            return;
                        }

                        //setting the question type
                        if(args[4].split(" ")[0].equals("-type")){
                            if(args[4].split(" ")[1].substring(1, args[4].split(" ")[1].length() - 1).equals("single")){
                                question.setSingle(true);
                            }
                            else{
                                question.setSingle(false);
                            }
                        }

                        //we check if the question already exists
                        if(firstQuestion != null && firstQuestion.findQuestion(question.question)){
                            System.out.println("{ 'status' : 'error', 'message' : 'Question already exists'}");
                            return;
                        }

                        i = 5;
                        int j = 0;
                        //from here we extract the answers and if they are correct
                        for(; i < args.length; i += 2){

                           String ans = "answer-" + (j + 1);

                            if(args[i].split(" ")[0].equals("-" + ans)){

                                if(j == 5){
                                    System.out.println("{ 'status' : 'error', 'message' : 'More than 5 answers were submitted'}");
                                    return;
                                }

                                String answer = args[i].split(" ")[1].substring(1, args[i].split(" ")[1].length() - 1);
                                //we set the answer to the selected position in the array
                                question.setAnswer(answer, j);
                                QuestionList.noOfAnsersTotal++;
                                //same for the id
                                question.setAnswerIDEach(QuestionList.noOfAnsersTotal, j);
                            }
                            else{
                                System.out.println("{ 'status' : 'error', 'message' : 'Answer " + (j + 1) + " has no answer description'}");
                                return;
                            }

                            //now we extract if the answer is correct
                            ans = ans + "-is-correct";
                            if(args[i + 1].split(" ")[0].equals("-" + ans)){
                                if(args[i + 1].split(" ")[1].substring(1, args[i + 1].split(" ")[1].length() - 1).equals("1")){
                                    question.setCorrect(true, j);
                                }
                                else{
                                    question.setCorrect(false, j);
                                }
                            }
                            else{
                                System.out.println("{ 'status' : 'error', 'message' : 'Answer " + (j + 1) + " has no answer correct flag'}");
                                return;
                            }

                            j++;

                        }
                        question.numberOfAnswers = j;

                        if(!question.checkCorrect()){
                            System.out.println("{ 'status' : 'error', 'message' : 'Single correct answer question has more than one correct answer'}");
                            return;
                        }

                        if(!question.checkDuplicates()){
                            System.out.println("{ 'status' : 'error', 'message' : 'Same answer provided more than once'}");
                            return;
                        }

                        if(j == 1){
                            System.out.println("{ 'status' : 'error', 'message' : 'Only one answer provided'}");
                            return;
                        }

                        //adds the question to the list
                        if(firstQuestion == null){
                            firstQuestion = question;
                        }
                        if(previousQuestion != null){
                            previousQuestion.setNext(question);
                        }
                        previousQuestion = question;
                        System.out.println("{ 'status' : 'ok', 'message' : 'Question added successfully'}");

                    }
                    else{
                        System.out.println("{ 'status' : 'error', 'message' : 'No answer provided'}");
                        return;
                    }
                }

            }

        }

        if(args[0].equals("-get-question-id-by-text")){

            //checking the login credentials
            if(args.length < 3){
                System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
                return;
            }

            AccountList acc = new AccountList();
            String user;
            String pass;

            if(args[1].split(" ")[0].equals("-u")){
                user = args[1].split(" ")[1].substring(1, args[1].split(" ")[1].length() - 1);
                acc.setUsername(user);
            }
            if(args[2].split(" ")[0].equals("-p")){
                pass = args[2].split(" ")[1].substring(1, args[2].split(" ")[1].length() - 1);
                acc.setPassword(pass);
            }

            if(first == null || !first.checkPassword(acc)){
                System.out.println("{ 'status' : 'error', 'message' : 'Login failed'}");
                return;
            }

            if(args[3].split(" ")[0].equals("-text")){
                //extracts the title of the question
                String text = args[3].split("'")[1];

                //checks if the question exists
                if(firstQuestion == null || !firstQuestion.findQuestion(text)){
                    System.out.println("{ 'status' : 'error', 'message' : 'Question does not exist'}");
                    return;
                }

                //if it exists we print the id
                int i = firstQuestion.getQuestionID(text, 1);
                System.out.println("{ 'status' : 'ok', 'message' : '" + i + "'}");

            }

        }

        if(args[0].equals("-get-all-questions")){

            //checking the login credentials
            if(args.length < 3){
                System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
                return;
            }

            AccountList acc = new AccountList();
            String user;
            String pass;

            if(args[1].split(" ")[0].equals("-u")){
                user = args[1].split(" ")[1].substring(1, args[1].split(" ")[1].length() - 1);
                acc.setUsername(user);
            }
            if(args[2].split(" ")[0].equals("-p")){
                pass = args[2].split(" ")[1].substring(1, args[2].split(" ")[1].length() - 1);
                acc.setPassword(pass);
            }

            if(first == null || !first.checkPassword(acc)){
                System.out.println("{ 'status' : 'error', 'message' : 'Login failed'}");
                return;
            }

            //prints all questions
            System.out.print("{ 'status' : 'ok', 'message' : '[");
            firstQuestion.printQuestion(1);
            System.out.println("]'}");

        }

        if(args[0].equals("-create-quizz")) {

            //checking the login credentials
            QuizzList quizz = new QuizzList();
            if (args.length < 3) {
                System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
                return;
            }

            AccountList acc = new AccountList();

            String user;
            String pass;

            if (args[1].split(" ")[0].equals("-u")) {
                user = args[1].split(" ")[1].substring(1, args[1].split(" ")[1].length() - 1);
                acc.setUsername(user);
            }
            if (args[2].split(" ")[0].equals("-p")) {
                pass = args[2].split(" ")[1].substring(1, args[2].split(" ")[1].length() - 1);
                acc.setPassword(pass);
            }

            if (first == null || !first.checkPassword(acc)) {
                System.out.println("{ 'status' : 'error', 'message' : 'Login failed'}");
                return;
            }

            //adds the user to the quiz
            quizz.user = acc;

            if (args[3].split(" ")[0].equals("-name")) {
                //extracts the name of the quiz and checks if it already exists
                String name = args[3].split("'")[1];
                if(firstQuizz != null && firstQuizz.findQuizz(name)){
                    System.out.println("{ 'status' : 'error', 'message' : 'Quizz name already exists'}");
                    return;
                }
                quizz.setName(name);
            }

            int j = 0;
            for(int i = 4; i < args.length; i++){

                //error for more than 10 questions
                if(j == 11){
                    System.out.println("{ 'status' : 'error', 'message' : 'Too many questions'}");
                    return;
                }

                String qst = "-question-" + (j + 1);
                if(args[i].split(" ")[0].equals(qst)){
                    String idStr = args[i].split("'")[1];
                    //extracts the question id
                    int id = Integer.parseInt(idStr);
                    quizz.setQuestionIDArrayEach(id, j);
                    //gets the question and adds it
                    QuestionList question = firstQuestion.getQuestion(id, 1);
                    if(question == null){
                        System.out.println("{ 'status' : 'error', 'message' : 'Question ID for question " + id + " does not exist'}");
                        return;
                    }
                    quizz.setQuestionsEach(question, j);
                    j++;
                }
            }
            quizz.numberOfQuestions = j;

            //adds the quiz to the list
            if(firstQuizz == null){
                firstQuizz = quizz;
            }
            if(previousQuizz != null){
                previousQuizz.setNext(quizz);
            }
            previousQuizz = quizz;
            System.out.println("{ 'status' : 'ok', 'message' : 'Quizz added succesfully'}");

        }

        if(args[0].equals("-get-quizz-by-name")){

            //checking the login credentials
            if(args.length < 3){
                System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
                return;
            }

            AccountList acc = new AccountList();
            String user;
            String pass;

            if(args[1].split(" ")[0].equals("-u")){
                user = args[1].split(" ")[1].substring(1, args[1].split(" ")[1].length() - 1);
                acc.setUsername(user);
            }
            if(args[2].split(" ")[0].equals("-p")){
                pass = args[2].split(" ")[1].substring(1, args[2].split(" ")[1].length() - 1);
                acc.setPassword(pass);
            }

            if(first == null || !first.checkPassword(acc)){
                System.out.println("{ 'status' : 'error', 'message' : 'Login failed'}");
                return;
            }

            if(args[3].split(" ")[0].equals("-name")){
                String name = args[3].split("'")[1];

                //extracts the name
                if(firstQuizz == null || !firstQuizz.findQuizz(name)){
                    System.out.println("{ 'status' : 'error', 'message' : 'Quizz does not exist'}");
                    return;
                }

                //prints the quiz id
                int id = firstQuizz.getQuizzByName(name, 1);
                System.out.println("{ 'status' : 'ok', 'message' : '" + id + "'}");

            }

        }

        if(args[0].equals("-get-all-quizzes")){

            //checking the login credentials
            if(args.length < 3){
                System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
                return;
            }

            AccountList acc = new AccountList();
            String user;
            String pass;

            if(args[1].split(" ")[0].equals("-u")){
                user = args[1].split(" ")[1].substring(1, args[1].split(" ")[1].length() - 1);
                acc.setUsername(user);
            }
            if(args[2].split(" ")[0].equals("-p")){
                pass = args[2].split(" ")[1].substring(1, args[2].split(" ")[1].length() - 1);
                acc.setPassword(pass);
            }

            if(first == null || !first.checkPassword(acc)){
                System.out.println("{ 'status' : 'error', 'message' : 'Login failed'}");
                return;
            }

            //prints the list of quizzes
            System.out.print("{ 'status' : 'ok', 'message' : '[");
            firstQuizz.printAllQuizzes(1, first);
            System.out.println("]'}");

        }

        if(args[0].equals("-get-quizz-details-by-id")){

            //checking the login credentials
            if(args.length < 3){
                System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
                return;
            }

            AccountList acc = new AccountList();
            String user;
            String pass;

            if(args[1].split(" ")[0].equals("-u")){
                user = args[1].split(" ")[1].substring(1, args[1].split(" ")[1].length() - 1);
                acc.setUsername(user);
            }
            if(args[2].split(" ")[0].equals("-p")){
                pass = args[2].split(" ")[1].substring(1, args[2].split(" ")[1].length() - 1);
                acc.setPassword(pass);
            }

            if(first == null || !first.checkPassword(acc)){
                System.out.println("{ 'status' : 'error', 'message' : 'Login failed'}");
                return;
            }

            if(args[3].split(" ")[0].equals("-id")) {
                //extracts the id
                String idStr = args[3].split("'")[1];
                int id = Integer.parseInt(idStr);

                //checks if the quiz exists
                if(id > QuizzList.noOfQuizzesTotal){
                    System.out.println("{ 'status' : 'error', 'message' : 'Quizz does not exist'}");
                    return;
                }

                //prints the details of the quiz
                System.out.print("{'status':'ok','message':'[");
                firstQuizz.getQuizzByID(id, 1);
                System.out.println("]'}");
            }
        }

        if(args[0].equals("-submit-quizz")) {

            //checking the login credentials
            String acc = Tema1.authenticate(args, first);
            if (acc == null)
                return;

            if (args.length < 4) {
                System.out.println("{ 'status' : 'error', 'message' : 'No quizz identifier was provided'}");
                return;
            }

            if (args[3].split(" ")[0].equals("-quiz-id")) {
                //extracts the quiz id
                String idStr = args[3].split("'")[1];
                int id = Integer.parseInt(idStr);
                if (!firstQuizz.findQuizByID(id, 1)) {
                    System.out.println("{ 'status' : 'error', 'message' : 'No quiz was found'}");
                    return;
                }

                //checks if the quiz if made by user
                if (firstQuizz.checkIfUserHasQuiz(acc, id)) {
                    System.out.println("{ 'status' : 'error', 'message' : 'You cannot answer your own quizz'}");
                    return;
                }

                //checks if the quiz is already answered
                if (AccountList.checkIfUserCompletedQuiz(acc, id, first)) {
                    System.out.println("{ 'status' : 'error', 'message' : 'You already submitted this quizz'}");
                    return;
                }

                QuizzList q = firstQuizz.getQuizzByIDReturn(id, 1);
                double score = 0;

                //calculating the percentage of each question at 2 decimals
                double p = 100d / q.numberOfQuestions;
                p *= 100;
                int percentagePerQuestionInt = (int) p;
                double percentagePerQuestion = percentagePerQuestionInt / 100d;

                for (int i = 4; i < args.length; i++) {

                    //extract each answer and search through the questions
                    String txt = "-answer-id-" + (i - 3);
                    if (args[i].split(" ")[0].equals(txt)) {
                        String idAnsStr = args[i].split("'")[1];
                        int idAns = Integer.parseInt(idAnsStr);
                        //we get the score
                        score = q.checkAnswer(idAns, firstQuestion, score, percentagePerQuestion);

                    }

                }

                //rounding the score
                if (score > 0)
                    score = Math.round(score);
                else
                    score = 0;

                //last we need to add the score to the users scores
                AccountList.addScore(q.numberOfQuestions, acc, id, score, first);
                System.out.println("{ 'status' : 'ok', 'message' : '" + (int) score + " points'}");

            }

        }

        if(args[0].equals("-delete-quizz-by-id")){

            //checking the login credentials
            String acc = Tema1.authenticate(args, first);
            if(acc == null)
                return;

            if(args.length > 3){
                if(args[3].split(" ")[0].equals("-id")){
                    //extracts the id
                    String idStr = args[3].split("'")[1];
                    int id = Integer.parseInt(idStr);
                    //checks if the quiz exists
                    if(id > QuizzList.noOfQuizzesTotal){
                        System.out.println("{ 'status' : 'error', 'message' : 'No quiz was found'}");
                        return;
                    }
                    else{
                        //if we have only one quiz we can do it manually
                        if(QuizzList.noOfQuizzesTotal == 1){
                            firstQuizz = null;
                            previousQuizz = null;
                            QuizzList.noOfQuizzesTotal = 0;
                        }
                        else{
                            //if we have more than one quiz we need to delete it from the list
                            firstQuizz.deleteQuiz(id, 1);
                        }
                        System.out.println("{ 'status' : 'ok', 'message' : 'Quizz deleted successfully'}");
                    }
                }
            }
            else{
                System.out.println("{ 'status' : 'error', 'message' : 'No quizz identifier was provided'}");
                return;
            }

        }

        if(args[0].equals("-get-my-solutions")){

            //checking the login credentials
            String acc = Tema1.authenticate(args, first);
            if(acc == null)
                return;

            //we start printing
            System.out.print("{ 'status' : 'ok', 'message' : '[");
            AccountList usr = first.retUsr(acc);
            usr.printSolutions(firstQuizz);
            System.out.println("]'}");

        }

        //writing the users to the file
        try ( FileWriter fw = new FileWriter("./user.csv", false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
                while(first != null){
                    out.println(first.toString());
                    first = first.next;
                }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //writing the questions to the file
        try ( FileWriter fw = new FileWriter("./question.csv", false);
              BufferedWriter bw = new BufferedWriter(fw);
              PrintWriter out = new PrintWriter(bw)) {
            while(firstQuestion != null){
                out.println(firstQuestion.toString());
                firstQuestion = firstQuestion.next;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //writing the quizzes to the file
        try ( FileWriter fw = new FileWriter("./quizz.csv", false);
              BufferedWriter bw = new BufferedWriter(fw);
              PrintWriter out = new PrintWriter(bw)) {
            while(firstQuizz != null){
                out.println(firstQuizz.toString());
                firstQuizz = firstQuizz.next;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //deletes everything from files
        if(args[0].equals("-cleanup-all")){
            try ( FileWriter fw = new FileWriter("./user.csv", false);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw)) {
                    out.println("");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            try ( FileWriter fw = new FileWriter("./question.csv", false);
                  BufferedWriter bw = new BufferedWriter(fw);
                  PrintWriter out = new PrintWriter(bw)) {
                out.println("");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            try ( FileWriter fw = new FileWriter("./quizz.csv", false);
                  BufferedWriter bw = new BufferedWriter(fw);
                  PrintWriter out = new PrintWriter(bw)) {
                out.println("");
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("{ 'status' : 'ok', 'message' : 'Cleanup finished successfully' }");
        }

    }

    public static String authenticate(String[] args, AccountList first){

        //here we check if we have user credentials as parameters
        if(args.length < 3){
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return null;
        }

        AccountList acc = new AccountList();
        String user;
        String pass;

        if(args[1].split(" ")[0].equals("-u")){
            user = args[1].split(" ")[1].substring(1, args[1].split(" ")[1].length() - 1);
            acc.setUsername(user);
        }
        if(args[2].split(" ")[0].equals("-p")){
            pass = args[2].split(" ")[1].substring(1, args[2].split(" ")[1].length() - 1);
            acc.setPassword(pass);
        }
        //we create a user to check if the credentials are correct
        if(first == null || !first.checkPassword(acc)){
            System.out.println("{ 'status' : 'error', 'message' : 'Login failed'}");
            return null;
        }
        //ew return the username
        return acc.username;
    }

}
