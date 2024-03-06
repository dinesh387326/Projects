/*Code for functional regression*/
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
    double *yin = new double [n];
    double *xin = new double [n];
    double *yp = new double [n];

    for(int i = 0;i<n;i++){
        rf>>xi[i];
    }
    for(int i = 0;i<n;i++){
        rf>>yi[i];
    }
    for(int i= 0 ; i<n;i++){
        xin[i] = xi[i]/100.0;
    }
    for(int i= 0 ; i<n;i++){
        yin[i] = 1.0/yi[i];
    }

    double sum_xi = 0;
    double sum_yi = 0;
    double sum_xi_sq = 0;
    double sum_xiyi = 0;

    for(int i = 0; i<n; i++){
        sum_xi = sum_xi + xin[i];
        sum_yi = sum_yi + yin[i];
        sum_xi_sq = sum_xi_sq + xin[i]*xin[i];
        sum_xiyi = sum_xiyi + xin[i]*yin[i];
    }

    double a1;
    double a0;
    double ULFL1; 
    double ULFL2;

    a1 = ((n*sum_xiyi) - (sum_xi*sum_yi))/ ((n*sum_xi_sq)-(sum_xi*sum_xi));
    a0 = ((sum_yi/n)-(a1*sum_xi/n));

    ULFL2 = 1.0/a0;
    ULFL1 = 1.0/(a1 + a0);

    for(int i= 0; i<n ; i++){
        yp[i] = 1.0/(a0 + (a1*xin[i]));
    }
    
    wf<<"Predicted Le Chatelier's values : "<<endl;
    for(int i = 0 ; i<n; i++){
        wf<<yp[i]<<" ,";
    }

    wf<<endl;

    wf<<endl<<"a1 :"<<a1;
    wf<<endl<<"a0 :"<<a0;
    wf<<endl<<"UFL's/LFL's of 1 :"<<ULFL1;
    wf<<endl<<"UFL's/LFL's of 2 :"<<ULFL2;

    rf.close();
    wf.close();

    return 0;
}