/*Code for linear regression*/
#include<iostream>
#include<cmath>
#include<fstream>
#include<iomanip>

using namespace std;

int main(){
    ofstream wf("Output.txt");
    ifstream rf("Input.txt");

    int n;
    rf>>n;

    double *xi = new double [n];
    double *yi = new double [n];
    double *yp = new double [n];

    for(int i = 0;i<n;i++){
        rf>>xi[i];
    }
    for(int i = 0;i<n;i++){
        rf>>yi[i];
    }

    double sum_xi = 0;
    double sum_yi = 0;
    double sum_xi_sq = 0;
    double sum_xiyi = 0;

    for(int i = 0; i<n; i++){
        sum_xi = sum_xi + xi[i];
        sum_yi = sum_yi + yi[i];
        sum_xi_sq = sum_xi_sq + xi[i]*xi[i];
        sum_xiyi = sum_xiyi + xi[i]*yi[i];
    }

    double a1;
    double a0;

    a1 = ((n*sum_xiyi) - (sum_xi*sum_yi))/ ((n*sum_xi_sq)-(sum_xi*sum_xi));
    a0 = ((sum_yi/n)-(a1*sum_xi/n));

    for(int i= 0; i<n ; i++){
        yp[i] = (a0 + (a1*xi[i]));
    }

    wf<<"Predicted Le Chatelier's values : "<<endl;
    for(int i = 0 ; i<n; i++){
        wf<<yp[i]<<" ,";
    }

    wf<<endl;

    wf<<endl<<"a1 :"<<a1;
    wf<<endl<<"a0 :"<<a0;

    rf.close();
    wf.close();

    return 0;
}