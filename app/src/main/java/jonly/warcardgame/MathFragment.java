package jonly.warcardgame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by wintow on 3/30/2018.
 */

public class MathFragment extends Fragment {

    private int correctAnswer;
    private String equation;

    private Random rand;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        rand = new Random();

        View fragmentView = inflater.inflate(R.layout.math_fragment, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        GenerateMath(); // generate the new math problem/answer

        Button button = (Button) getView().findViewById(R.id.math_input_button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                EditText userAnswer = getView().findViewById(R.id.math_input);
                TextView equationView = getView().findViewById(R.id.equation);
                LinearLayout answeredList = getView().findViewById(R.id.correct_ans_list);

                String ans = userAnswer.getText().toString();
                String solution = equationView.getText().toString() + " = " + correctAnswer;

                if(ans.equals(correctAnswer + "")){
                    TextView add_solution = new TextView(getActivity());
                    add_solution.setText(solution);
                    answeredList.addView(add_solution, 0);
                    GenerateMath();
                }

                userAnswer.setText("");
            }
        });
    }

    private void GenerateMath(){
        TextView equationView = getView().findViewById(R.id.equation);

        correctAnswer = -1;
        equation = "";

        while(correctAnswer < 0){
            int firstNum = rand.nextInt(16), secondNum = rand.nextInt(16);

            switch(rand.nextInt(3)){
                case 0: // addition
                    correctAnswer = firstNum + secondNum;
                    equation = firstNum + " + " + secondNum;
                    break;
                case 1: // subtraction
                    correctAnswer = firstNum - secondNum;
                    equation = firstNum + " - " + secondNum;
                    break;
                case 2: // multiplication
                    correctAnswer = firstNum * secondNum;
                    equation = firstNum + " * " + secondNum;
                    break;
            }
        }

        equationView.setText(equation);
    }
}
