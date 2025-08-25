const flow_rates_reactant = molesReactant;
const reaction_orders = relativeOrder;
const stoichiometery_reactant = stoichiometricReactant;
const stoichiometery_product = stoichiometricProduct;
let epsilon = 0;
const reaction_type = phase;
const Initial_Conversion = 0;
const Final_Conversion = finalConversion;
let volumetric_sum = volOfReactor;
const rate_constant = k;

// Basis Calculation
let nv = stoichiometery_reactant.length;
let np = stoichiometery_product.length;

let basis = flow_rates_reactant[0] / stoichiometery_reactant[0];
let basis_index = 0;
for (let i = 0; i < nv; i++) {
    if (basis > flow_rates_reactant[i] / stoichiometery_reactant[i]) {
        basis = flow_rates_reactant[i] / stoichiometery_reactant[i];
        basis_index = i;
    }
}

let Ca0 = flow_rates_reactant[basis_index] / volumetric_sum;
let Fa0 = flow_rates_reactant[basis_index];

let flow_rates_ratio = [];
let niu_reactant = [];
let niu_product = [];

let niu_reactant_sum = 0;
let niu_product_sum = 0;

for (let i = 0; i < nv; i++) {
    flow_rates_ratio.push(flow_rates_reactant[i] / basis);
    niu_reactant.push(stoichiometery_reactant[i] / stoichiometery_reactant[basis_index]);
    niu_reactant_sum += niu_reactant[i];
}

for (let i = 0; i < np; i++) {
    niu_product.push(stoichiometery_product[i] / stoichiometery_reactant[basis_index]);
    niu_product_sum += niu_reactant[i];
}

// Epsilon calculation
epsilon = (niu_product_sum - niu_reactant_sum) * (flow_rates_reactant[basis_index] / volumetric_sum);
console.log(niu_reactant, niu_product, volumetric_sum, flow_rates_reactant)

function Integrating_func_liquid(x, Fr, Nr, Ca0, Fa0, Ro, n, v, e, k) {
    if (n > 0) {
        n = n - 1;
    } else {
        return Fa0 / k;
    }
    return (1.0 / (Math.pow(Ca0 * (Fr[n] - Nr[n] * x), Ro[n]))) * Integrating_func_liquid(x, Fr, Nr, Ca0, Fa0, Ro, n, v, e, k);
}

function Integrating_func_gases(x, Fr, Nr, Ca0, Fa0, Ro, n, v, e, k) {
    if (n > 0) {
        n = n - 1;
    } else {
        return Fa0 / k;
    }
    return (1.0 / (Math.pow(Ca0 * (Fr[n] - Nr[n] * x) / (1 + e * x), Ro[n]))) * Integrating_func_gases(x, Fr, Nr, Ca0, Fa0, Ro, n, v, e, k);
}

function Volume_PFR(Fr, Nr, Ca0, Fa0, Ro, n, v, e, k, type) {
    if (type == "Liquid") {
        return function integrand(x) {
            return Integrating_func_liquid(x, Fr, Nr, Ca0, Fa0, Ro, n, v, e, k);
        };
    } else {
        return function integrand(x) {
            return Integrating_func_gases(x, Fr, Nr, Ca0, Fa0, Ro, n, v, e, k);
        };
    }
}
console.log(epsilon)

// Trapezoidal rule integration
function trapezoidalIntegration(func, a, b, n) {
    let h = (b - a) / n;
    let sum = 0.5 * (func(a) + func(b));
    for (let i = 1; i < n; i++) {
        sum += func(a + i * h);
    }
    return h * sum;
}

// Integrate
let integrandFunc = Volume_PFR(flow_rates_ratio, niu_reactant, Ca0, Fa0, reaction_orders, nv, volumetric_sum, epsilon, rate_constant, reaction_type);
let result = trapezoidalIntegration(integrandFunc, Initial_Conversion, Final_Conversion, 1000);
setOutput(result)