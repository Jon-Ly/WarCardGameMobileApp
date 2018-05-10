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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.util.Random;

/**
 * Created by wintow on 3/30/2018.
 */

public class MathFragment extends Fragment {

    private String insert_math_url = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?insertMath=";
    private TextView equation_view;
    private int answer;
    private int correct_counter;
    private String equation;
    private String correct_answers;

    private Random rand;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        rand = new Random(56);
        equation = getNewEquation(); // generate the new math problem/answer
        correct_counter = 0;
        if(savedInstanceState != null) {
            answer = savedInstanceState.getInt("Answer");
            equation = savedInstanceState.getString("Equation");
            correct_answers = savedInstanceState.getString("Correct_Answers");
        }else{
            correct_answers = "";
        }
        View fragmentView = inflater.inflate(R.layout.math_fragment, container, false);
        return fragmentView;
    }

    @Override
    public void onStart(){
        super.onStart();

        if(equation_view == null){
            equation_view = getActivity().findViewById(R.id.equation);
            equation_view.setText(equation);
        }

        if(getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE){
            LinearLayout answeredList = getActivity().findViewById(R.id.correct_ans_list);
            String[] answers = correct_answers.split("\n");
            for(String s : answers) {
                TextView add_solution = new TextView(getActivity());
                add_solution.setText(s);
                answeredList.addView(add_solution, 0);
            }
        }

        Button button = (Button) getView().findViewById(R.id.math_input_button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                EditText userAnswer = getView().findViewById(R.id.math_input);
                LinearLayout answeredList = getView().findViewById(R.id.correct_ans_list);

                String ans = userAnswer.getText().toString();
                String solution = equation_view.getText().toString() + " = " + answer;

                if(ans.equals(answer + "")){
                    if (answeredList != null) {
                        TextView add_solution = new TextView(getActivity());
                        add_solution.setText(solution);
                        answeredList.addView(add_solution, 0);
                    }
                    correct_answers += solution + "\n";
                    correct_counter++;
                    equation = getNewEquation();
                    equation_view.setText(equation);
                }

                if(correct_counter >= 3){
                    RequestQueue queue = Volley.newRequestQueue(getContext());

                    TextView username_label_view = (TextView) getView().findViewById(R.id.username_label);
                    String username = username_label_view.getText().toString();

                    Toast.makeText(getContext(), username + ", you win!", Toast.LENGTH_SHORT).show();

                    StringRequest string_request = new StringRequest(Request.Method.GET, insert_math_url + username, new Response.Listener<String>() {
                        public void onResponse(String response) {

                        }

                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError er) {

                        }
                    });

                    queue.add(string_request);
                }
                userAnswer.setText("");
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("Answer", answer);
        savedInstanceState.putString("Equation", equation);
        savedInstanceState.putString("Correct_Answers", correct_answers);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

//        GenerateMath(); // generate the new math problem/answer
//
//        Button button = (Button) getView().findViewById(R.id.math_input_button);
//        button.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                EditText userAnswer = getView().findViewById(R.id.math_input);
//                TextView equationView = getView().findViewById(R.id.equation);
//                LinearLayout answeredList = getView().findViewById(R.id.correct_ans_list);
//
//                String ans = userAnswer.getText().toString();
//                String solution = equationView.getText().toString() + " = " + answer;
//
//                if(ans.equals(answer + "")){
//                    TextView add_solution = new TextView(getActivity());
//                    add_solution.setText(solution);
//                    answeredList.addView(add_solution, 0);
//                    GenerateMath();
//                }
//
//                userAnswer.setText("");
//            }
//        });
    }

    private String getNewEquation(){
        answer = -1;
        equation = "";

        while(answer < 0){
            int firstNum = rand.nextInt(16), secondNum = rand.nextInt(16);

            switch(rand.nextInt(3)){
                case 0: // addition
                    answer = firstNum + secondNum;
                    equation = firstNum + " + " + secondNum;
                    break;
                case 1: // subtraction
                    answer = firstNum - secondNum;
                    equation = firstNum + " - " + secondNum;
                    break;
                case 2: // multiplication
                    answer = firstNum * secondNum;
                    equation = firstNum + " * " + secondNum;
                    break;
            }
        }

        return equation;
    }

    public void resetCounter(){
        this.correct_counter = 0;
    }
}
