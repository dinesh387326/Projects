// Defined Variables
const flowRatesReactant = [1.0, 1.0];
const reactionOrders = [2.0, 1.0];
const volumetricFlowReactant = [0.25, 0.25];
const stoichiometryReactant = [1.0, 1.0];
const stoichiometryProduct = [1.0, 1.0];
let epsilon = 0;
const reactionType = "liquid";
const initialConversion = 0;
const finalConversion = 0.8;
let volumetricSum = 0.0;
const rateConstant = 0.1;

// Basis Calculation
const nv = volumetricFlowReactant.length;
const np = stoichiometryProduct.length;

let basis = flowRatesReactant[0] / stoichiometryReactant[0];
let basisIndex = 0;

for (let i = 0; i < nv; i++) {
  if (basis > flowRatesReactant[i] / stoichiometryReactant[i]) {
    basis = flowRatesReactant[i] / stoichiometryReactant[i];
    basisIndex = i;
  }
  volumetricSum += volumetricFlowReactant[i];
}

const Ca0 = flowRatesReactant[basisIndex] / volumetricSum;

const flowRatesRatio = flowRatesReactant.map(rate => rate / basis);
const niuReactant = stoichiometryReactant.map(stoichiometry => stoichiometry / basis);
const niuProduct = stoichiometryProduct.map(stoichiometry => stoichiometry / basis);

let niuReactantSum = niuReactant.reduce((sum, value) => sum + value, 0);
let niuProductSum = niuProduct.reduce((sum, value) => sum + value, 0);

// Epsilon calculation
epsilon = (niuProductSum - niuReactantSum) * (flowRatesReactant[basisIndex] / volumetricSum);

// Integration function for liquid

if (reactionType==='liquid'){
    epsilon=0
}
// Integration function for gases
function integratingFuncGases(x, Fr, Nr, Ca0, Ro, n, v, e, k) {
  if (n > 0) {
    n = n - 1;
  } else {
    return 1.0 / k;
  }
  return (1.0 / (((Ca0 * (Fr[n] - Nr[n] * x)) / (1 + e * x)) ** Ro[n])) * integratingFuncGases(x, Fr, Nr, Ca0, Ro, n, v, e, k);
}

// Volume PFR function


// Definite integral calculation

const result =Ca0*volumetricSum* finalConversion* integratingFuncGases(finalConversion, flowRatesRatio, niuReactant, Ca0, reactionOrders, nv, volumetricSum, epsilon, rateConstant, reactionType);
console.log(`The definite integral from ${initialConversion} to ${finalConversion} is: ${result.toFixed(6)}`);
