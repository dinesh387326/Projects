import React, { useState } from "react";

const DropdownExample = () => {
  const [selectedOption, setSelectedOption] = useState(''); // Initialize state for selected option
  const [Reactants, setReactants] = useState(0);
  const [Products, setProducts] = useState(0);
  const [phase, setPhase] = useState('');
  const [molesReactant, setMolesReactant] = useState([]);
  const [molesProduct, setMolesProduct] = useState([]);
  const [stoichiometricReactant, setStoichiometricReactant] = useState([]); // State for stoichiometric reactant coefficients
  const [stoichiometricProduct, setStoichiometricProduct] = useState([]); // State for stoichiometric product coefficients
  const [volReactant, setVolReactant] = useState([]); // State for volume/volumetric flow of reactant
  const [volProduct, setVolProduct] = useState([]); // State for volume/volumetric flow of product
  const [relativeOrder, setRelativeOrder] = useState([]); // State for relative order of reactants
  const [initialPressure, setInitialPressure] = useState(0); // Initial pressure state
  const [initialTemperature, setInitialTemperature] = useState(0); // Initial temperature state
  const [pressureType, setPressureType] = useState(''); // Pressure type state
  const [phi, setPhi] = useState(0); // Phi state
  const [phoGas, setPhoGas] = useState(0); // PhoGas state
  const [particleDiameter, setParticleDiameter] = useState(0); // Particle diameter state
  const [viscosity, setViscosity] = useState(0); // Viscosity state
  const [area, setArea] = useState(0); // Area state
  const [phoCatalyst, setPhoCatalyst] = useState(0); // PhoCatalyst state
  const [volOfReactor, setVolOfReactor]=useState(0)
  const [k, setK]=useState(0)
  const [finalConversion, setFinalConversion]=useState(0)
  const [density, setDensity]=useState(0)
  const [output, setOutput]=useState(0)
  const [Result, setResult]=useState(0)

  const handleReactants = (event) => {
    setReactants(parseInt(event.target.value)); // Convert the input value to an integer
  };
  const handleFinalConversion = (event) => {
    setFinalConversion(parseInt(event.target.value)); // Convert the input value to an integer
  };


  const handleProducts = (event) => {
    setProducts(parseInt(event.target.value));
  };

  const handleChange = (event) => {
    setSelectedOption(event.target.value); // Update selected option when the dropdown value changes
  };
  const handleK=(event) => {
    setK(event.target.value); // Update selected option when the dropdown value changes
  };

  const moles = (name, variable, molesArray, setMolesArray, stoichiometricArray, setStoichiometricArray, volArray, setVolArray) => {
    const inputs = [];
    for (let i = 0; i < variable; i++) {
      inputs.push(
        <div className="ml-4">
          <h2 className="font-bold">FOR {name} {i+1}</h2>
        <div key={i} className="flex flex-col items-start flex-wrap my-5">
          <label className="my-2" htmlFor={`stoichiometric${name}${i + 1}`}>{`Stoichiometric coefficient of ${name} ${i + 1}: `}</label>
          <input
            type="number"
            id={`stoichiometric${name}${i + 1}`}
            onChange={(e) => handleStoichiometricChange(i, e.target.value, stoichiometricArray, setStoichiometricArray)}
          />
          <label className="my-2" htmlFor={`moles${name}${i + 1}`}>{`${selectedOption === 'Batch' ? 'Initial Moles of' : 'Initial Molar Flow Rate'} ${name} ${i + 1}: `}</label>
          <input
            type="number"
            id={`moles${name}${i + 1}`}
            onChange={(e) => handleMolesChange(i, e.target.value, molesArray, setMolesArray)}
          />
          {selectedOption !== 'Batch' && ( // Check if selected option is not 'Batch'
            <>
              <label className="my-2" htmlFor={`volume${name}${i + 1}`}>{`${selectedOption === 'Batch' ? 'Volume of' : 'Volumetric Flow Rate of'} ${name} ${i + 1}: `}</label>
              <input
                type="number"
                id={`volume${name}${i + 1}`}
                onChange={(e) => handleVolChange(i, e.target.value, volArray, setVolArray)}
              />
            </>
          )}
          {name === 'Reactant' && (
            <div>
              <label htmlFor={`relativeOrder${name}${i + 1}`}>{`Relative Order of ${name} ${i + 1}: `}</label>
              <input
                type="number"
                id={`relativeOrder${name}${i + 1}`}
                onChange={(e) => handleRelativeOrderChange(i, e.target.value)}
              />
            </div>
          )}
        </div>
        </div>
      );
    }
    return inputs;
  };

  const handleVolOfReactor =(event)=>{
    setVolOfReactor(parseInt(event.target.value))
  }

  const handleMolesChange = (index, value, molesArray, setMolesArray) => {
    const updatedMoles = [...molesArray];
    updatedMoles[index] = value;
    setMolesArray(updatedMoles);
  };

  const handleVolChange = (index, value, volArray, setVolArray) => {
    const updatedVol = [...volArray];
    updatedVol[index] = value;
    setVolArray(updatedVol);
  };

  const handleRelativeOrderChange = (index, value) => {
    const updatedOrder = [...relativeOrder];
    updatedOrder[index] = value;
    setRelativeOrder(updatedOrder);
  };

  const handleStoichiometricChange = (index, value, stoichiometricArray, setStoichiometricArray) => {
    const updatedStoichiometric = [...stoichiometricArray];
    updatedStoichiometric[index] = value;
    setStoichiometricArray(updatedStoichiometric);
  };

const BATCHVOLUME=()=>{
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
      flow_rates_ratio.push(flow_rates_reactant[i] / flow_rates_reactant[basis_index]);
      niu_reactant.push(stoichiometery_reactant[i] / stoichiometery_reactant[basis_index]);
      niu_reactant_sum += niu_reactant[i];
  }
  
  for (let i = 0; i < np; i++) {
      niu_product.push(stoichiometery_product[i] / stoichiometery_reactant[basis_index]);
      niu_product_sum += niu_product[i];

  }
  
  // Epsilon calculation
  epsilon = (niu_product_sum - niu_reactant_sum) * (flow_rates_reactant[basis_index] / volumetric_sum);
  
  function Integrating_func_liquid(x, Fr, Nr, Ca0, Fa0, Ro, n, v, e, k) {
      if (n > 0) {
          n = n - 1;
      } else {
          return Ca0 / k;
      }
      return (1.0 / (Math.pow(Ca0 * (Fr[n] - Nr[n] * x), Ro[n]))) * Integrating_func_liquid(x, Fr, Nr, Ca0, Fa0, Ro, n, v, e, k);
  }
  
  function Integrating_func_gases(x, Fr, Nr, Ca0, Fa0, Ro, n, v, e, k) {
      if (n > 0) {
          n = n - 1;
      } else {
          return  Ca0 / k;
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
  console.log(basis,Ca0, niu_reactant, flow_rates_ratio)
  let integrandFunc = Volume_PFR(flow_rates_ratio, niu_reactant, Ca0, Fa0, reaction_orders, nv, volumetric_sum, epsilon, rate_constant, reaction_type);
  let result = trapezoidalIntegration(integrandFunc, Initial_Conversion, Final_Conversion, 1000);
  setOutput(result)
}


const CSTRVOLUME=()=>{
  const flow_rates_reactant = molesReactant;
const reaction_orders = relativeOrder;
const volumetric_flow_reactant = volReactant;
const stoichiometery_reactant = stoichiometricReactant;
const stoichiometery_product = stoichiometricProduct;
let epsilon = 0;
const reaction_type = phase;
const Initial_Conversion = 0;
const Final_Conversion = finalConversion;
let volumetric_sum = 0.0;
const rate_constant = k;

// Basis Calculation
let nv = volumetric_flow_reactant.length;
let np = stoichiometery_product.length;

let basis = flow_rates_reactant[0] / stoichiometery_reactant[0];
let basis_index = 0;
for (let i = 0; i < nv; i++) {
    if (basis > flow_rates_reactant[i] / stoichiometery_reactant[i]) {
        basis = flow_rates_reactant[i] / stoichiometery_reactant[i];
        basis_index = i;
    }
}

for (let i = 0; i < nv; i++) {
    volumetric_sum += parseFloat(volumetric_flow_reactant[i]);
}

let Ca0 = flow_rates_reactant[basis_index] / volumetric_sum;
let Fa0 = flow_rates_reactant[basis_index];

let flow_rates_ratio = [];
let niu_reactant = [];
let niu_product = [];

let niu_reactant_sum = 0;
let niu_product_sum = 0;

for (let i = 0; i < nv; i++) {
    flow_rates_ratio.push(flow_rates_reactant[i] / flow_rates_reactant[basis_index]);
    niu_reactant.push(stoichiometery_reactant[i] / stoichiometery_reactant[basis_index]);
    console.log(niu_reactant)
    niu_reactant_sum += niu_reactant[i];
}

for (let i = 0; i < np; i++) {
    niu_product.push(stoichiometery_product[i] / stoichiometery_reactant[basis_index]);
    console.log(niu_product)
    niu_product_sum += niu_product[i];
}

// Epsilon calculation
epsilon = (niu_product_sum - niu_reactant_sum) * (flow_rates_reactant[basis_index] / volumetric_sum);
console.log(epsilon)
if (phase==='Liquid'){
  epsilon=0
}

function Integrating_func_gases(x, Fr, Nr, Ca0, Fa0, Ro, n, v, e, k) {
    if (n > 0) {
        n = n - 1;
    } else {
        return Fa0 / k;
    }
    return (1.0 / (Math.pow(Ca0 * (Fr[n] - Nr[n] * x) / (1 + e * x), Ro[n]))) * Integrating_func_gases(x, Fr, Nr, Ca0, Fa0, Ro, n, v, e, k);
}



// Trapezoidal rule integratio

// Integrate
let result = finalConversion* Integrating_func_gases(finalConversion, flow_rates_ratio, niu_reactant, Ca0, Fa0, reaction_orders, nv, volumetric_sum, epsilon, rate_constant);
setOutput(result)
}

  // Function to calculate volume for PFR
const PRFVOLUME = () => {
  // Defined Variables
const flow_rates_reactant = molesReactant;
const reaction_orders = relativeOrder;
const volumetric_flow_reactant = volReactant;
const stoichiometery_reactant = stoichiometricReactant;
const stoichiometery_product = stoichiometricProduct;
let epsilon = 0;
const reaction_type = phase;
const Initial_Conversion = 0;
const Final_Conversion = finalConversion;
let volumetric_sum = 0.0;
const rate_constant = k;

// Basis Calculation
let nv = volumetric_flow_reactant.length;
let np = stoichiometery_product.length;

let basis = flow_rates_reactant[0] / stoichiometery_reactant[0];
let basis_index = 0;
for (let i = 0; i < nv; i++) {
    if (basis > flow_rates_reactant[i] / stoichiometery_reactant[i]) {
        basis = flow_rates_reactant[i] / stoichiometery_reactant[i];
        basis_index = i;
    }
}

for (let i = 0; i < nv; i++) {
    volumetric_sum += parseFloat(volumetric_flow_reactant[i]);
}

let Ca0 = flow_rates_reactant[basis_index] / volumetric_sum;
let Fa0 = flow_rates_reactant[basis_index];

let flow_rates_ratio = [];
let niu_reactant = [];
let niu_product = [];

let niu_reactant_sum = 0;
let niu_product_sum = 0;

for (let i = 0; i < nv; i++) {
    flow_rates_ratio.push(flow_rates_reactant[i] / flow_rates_reactant[basis_index]);
    niu_reactant.push(stoichiometery_reactant[i] / stoichiometery_reactant[basis_index]);
    niu_reactant_sum += niu_reactant[i];
}

for (let i = 0; i < np; i++) {
    niu_product.push(stoichiometery_product[i] / stoichiometery_reactant[basis_index]);
    niu_product_sum += niu_product[i];

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
};

const PBRVOLUMEFINDER = () => {
  if (pressureType === 'Isobaric' || phase==='Liquid') {
    const flow_rates_reactant = molesReactant;
const reaction_orders = relativeOrder;
const volumetric_flow_reactant = volReactant;
const stoichiometery_reactant = stoichiometricReactant;
const stoichiometery_product = stoichiometricProduct;
let epsilon = 0;
const reaction_type = phase;
const Initial_Conversion = 0;
const Final_Conversion = finalConversion;
let volumetric_sum = 0.0;
const rate_constant = k;

// Basis Calculation
let nv = volumetric_flow_reactant.length;
let np = stoichiometery_product.length;

let basis = flow_rates_reactant[0] / stoichiometery_reactant[0];
let basis_index = 0;
for (let i = 0; i < nv; i++) {
    if (basis > flow_rates_reactant[i] / stoichiometery_reactant[i]) {
        basis = flow_rates_reactant[i] / stoichiometery_reactant[i];
        basis_index = i;
    }
}

for (let i = 0; i < nv; i++) {
    volumetric_sum += parseFloat(volumetric_flow_reactant[i]);
}

let Ca0 = flow_rates_reactant[basis_index] / volumetric_sum;
let Fa0 = flow_rates_reactant[basis_index];

let flow_rates_ratio = [];
let niu_reactant = [];
let niu_product = [];

let niu_reactant_sum = 0;
let niu_product_sum = 0;

for (let i = 0; i < nv; i++) {
    flow_rates_ratio.push(flow_rates_reactant[i] / flow_rates_reactant[basis_index]);
    niu_reactant.push(stoichiometery_reactant[i] / stoichiometery_reactant[basis_index]);
    niu_reactant_sum += niu_reactant[i];
}

for (let i = 0; i < np; i++) {
    niu_product.push(stoichiometery_product[i] / stoichiometery_reactant[basis_index]);
    niu_product_sum += niu_product[i];

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
let result =density* trapezoidalIntegration(integrandFunc, Initial_Conversion, Final_Conversion, 1000);
    setOutput(result); // Assuming density is defined
  }
};


  return (
  <div className='bg-gray-400 min-h-screen flex items-center justify-center flex-col '>
    <h1 className="text-5xl mt-12 mb-8 font-bold text-blue-900">!REACTOR CALCULATOR!</h1>
  <div className="p-8 bg-slate-200 rounded-lg shadow-xl mb-10">
        <div>
          <label htmlFor="dropdown" className="text-gray-700">Select the type of reactor:</label>
          <select id="dropdown" value={selectedOption} onChange={handleChange} className="block w-full mt-1 p-2 border border-gray-300 rounded">
            <option value="">Select...</option>
            <option value="Batch">Batch</option>
            <option value="CSTR">CSTR</option>
            <option value="PFR">PFR</option>
            <option value="PBR">PBR</option>
          </select>
        </div>
      <div>
        {selectedOption==='Batch' && <div><input type="number" value={volOfReactor} onChange={(e) => setVolOfReactor(parseFloat(e.target.value))}/>
        {volOfReactor}
        </div>}
      </div>
      <div className="my-2">
        No. of Reactants
        <input type="number" name="" id="reactants" onChange={handleReactants} />
      </div>
      <div className="my-2">
        No. of Products  
        <input type="number" name="" id="products" onChange={handleProducts} />
      </div>
      
      <div className="w-full">
        {Reactants > 0 && moles('Reactant', Reactants, molesReactant, setMolesReactant, stoichiometricReactant, setStoichiometricReactant, volReactant, setVolReactant)}
        
      </div>
      <div className="w-full">
        {Products > 0 && moles('Product', Products, molesProduct, setMolesProduct, stoichiometricProduct, setStoichiometricProduct, volProduct, setVolProduct)}
        
      </div>
      <div>
        Provide rate Constant of the reaction <input type="number" value={k} onChange={handleK} />
      </div>
      <div className="my-2">
        Phase of The reaction
        <select id="phaseDropdown" value={phase} onChange={(e) => setPhase(e.target.value)}>
          <option value="">Select...</option>
          <option value="Gas">Gas</option>
          <option value="Liquid">Liquid</option>
        </select>
        
      </div>
      <div>
        {phase === 'Gas' && (
          <div>
            Enter Initial Pressure:
            <input type="number" value={initialPressure} onChange={(e) => setInitialPressure(e.target.value)} />
            Enter Initial Temperature:
            <input type="number" value={initialTemperature} onChange={(e) => setInitialTemperature(e.target.value)} />
            
            {selectedOption === 'PBR' && (
              <div>
                Reaction type with respect to Pressure:
                <select value={pressureType} onChange={(e) => setPressureType(e.target.value)}>
                  <option value="">Select...</option>
                  <option value="Isobaric">Isobaric</option>
                  <option value="Non Isobaric">Non Isobaric</option>
                </select>
                
                {pressureType === 'Non Isobaric' && (
                  <div>
                    <input type="number" value={phi} onChange={(e) => setPhi(e.target.value)} />
                    <input type="number" value={phoGas} onChange={(e) => setPhoGas(e.target.value)} />
                    <input type="number" value={particleDiameter} onChange={(e) => setParticleDiameter(e.target.value)} />
                    <input type="number" value={viscosity} onChange={(e) => setViscosity(e.target.value)} />
                    <input type="number" value={area} onChange={(e) => setArea(e.target.value)} />
                    <input type="number" value={phoCatalyst} onChange={(e) => setPhoCatalyst(e.target.value)} />


                  </div>
                )}
              </div>
            )}
          </div>
        )}
        {selectedOption==='PBR'&& <div>
          Provide Density <input type="number" value={density} onChange={(e) => setDensity(e.target.value)} /></div>}
        <div>
         Provide Desired Conversion <input type="number" value={finalConversion} onChange={(e) => setFinalConversion(e.target.value)} />
        </div>
      </div>
        {selectedOption==='PFR'&& <button className="mt-4 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded" onClick={PRFVOLUME}> Find Volume</button>}
        {selectedOption==='CSTR'&& <button className="mt-4 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded" onClick={CSTRVOLUME}> Find Volume</button>}
        {selectedOption==='PBR'&& <button  className="mt-4 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded" onClick={PBRVOLUMEFINDER}> Find Weight</button>}
        {selectedOption==='Batch'&& <button className="mt-4 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded" onClick={BATCHVOLUME}> Find Volume</button>}

        {output > 0 &&  <h1 className="mt-4 text-xl font-bold">Volume: {output}</h1>}
      </div>
    </div>
  );
}

// Online Javascript Editor for free// Defined Variables

export default DropdownExample;
