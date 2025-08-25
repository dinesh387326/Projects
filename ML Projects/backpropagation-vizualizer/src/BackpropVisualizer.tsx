import React, { useEffect, useMemo, useRef, useState } from "react";

// =========================
// Utility math
// =========================
function sigmoid(x: number) { return 1 / (1 + Math.exp(-x)); }
function dSigmoid(y: number) { // y = sigmoid(x)
  return y * (1 - y);
}
function relu(x: number) { return Math.max(0, x); }
function dRelu(x: number) { return x > 0 ? 1 : 0; }
function tanh(x: number) { return Math.tanh(x); }
function dTanh(y: number) { return 1 - y * y; }

type ActName = "sigmoid" | "relu" | "tanh";

function activate(x: number, fn: ActName) {
  if (fn === "sigmoid") return sigmoid(x);
  if (fn === "relu") return relu(x);
  return tanh(x);
}
function dActivate(x: number, y: number, fn: ActName) {
  if (fn === "sigmoid") return dSigmoid(y);
  if (fn === "relu") return dRelu(x);
  return dTanh(y);
}

// nice color scale for activations in [0,1]
function lerp(a: number, b: number, t: number) { return a + (b - a) * t; }
function clamp01(x: number) { return Math.max(0, Math.min(1, x)); }
function valueToColor(v01: number) {
  const t = clamp01(v01);
  // interpolate between light gray -> sky -> green -> orange -> red
  const stops = [
    [230, 230, 230], // low
    [125, 211, 252], // sky-300
    [134, 239, 172], // green-300
    [253, 186, 116], // orange-300
    [248, 113, 113], // red-400
  ];
  const idx = Math.min(stops.length - 2, Math.floor(t * (stops.length - 1)));
  const localT = t * (stops.length - 1) - idx;
  const c0 = stops[idx];
  const c1 = stops[idx + 1];
  const r = Math.round(lerp(c0[0], c1[0], localT));
  const g = Math.round(lerp(c0[1], c1[1], localT));
  const b = Math.round(lerp(c0[2], c1[2], localT));
  return `rgb(${r}, ${g}, ${b})`;
}

// map weight to color: negative=purple, positive=blue
function weightColor(w: number) {
  const t = clamp01((w + 1) / 2); // map [-1,1] -> [0,1]
  const neg = [196, 181, 253]; // violet-300
  const pos = [96, 165, 250];  // blue-400
  const mid = [203, 213, 225]; // slate-300
  // interpolate via mid
  if (w < 0) {
    const tt = 1 - t;
    const r = Math.round(lerp(neg[0], mid[0], t));
    const g = Math.round(lerp(neg[1], mid[1], t));
    const b = Math.round(lerp(neg[2], mid[2], t));
    return `rgb(${r}, ${g}, ${b})`;
  }
  const r = Math.round(lerp(mid[0], pos[0], t));
  const g = Math.round(lerp(mid[1], pos[1], t));
  const b = Math.round(lerp(mid[2], pos[2], t));
  return `rgb(${r}, ${g}, ${b})`;
}

// =========================
// Types
// =========================
interface Net {
  layerSizes: number[]; // e.g., [2,3,2,1]
  W: number[][][]; // W[l][i][j] : from node i in layer l to node j in layer l+1
  b: number[][];   // b[l+1][j]
}

function initNet(layerSizes: number[]): Net {
  const W: number[][][] = [];
  const b: number[][] = [];
  for (let l = 0; l < layerSizes.length - 1; l++) {
    const inSize = layerSizes[l];
    const outSize = layerSizes[l + 1];
    const std = 0.5; // smaller std for stability
    const wLayer: number[][] = [];
    for (let i = 0; i < inSize; i++) {
      const row: number[] = [];
      for (let j = 0; j < outSize; j++) row.push((Math.random() * 2 - 1) * std);
      wLayer.push(row);
    }
    W.push(wLayer);
    b.push(Array(outSize).fill(0));
  }
  return { layerSizes, W, b };
}


// forward pass returning z and a by layer
function forward(net: Net, x: number[], act: ActName) {
  const a: number[][] = [x.slice()];
  const z: number[][] = [];
  for (let l = 0; l < net.W.length; l++) {
    const inSize = net.layerSizes[l];
    const outSize = net.layerSizes[l + 1];
    const zVec: number[] = new Array(outSize).fill(0);
    for (let j = 0; j < outSize; j++) {
      let s = net.b[l][j];
      for (let i = 0; i < inSize; i++) s += net.W[l][i][j] * a[l][i];
      zVec[j] = s;
    }
    const aNext = zVec.map((zz) => activate(zz, act));
    z.push(zVec);
    a.push(aNext);
  }
  return { a, z };
}

// backprop for single (x,y), MSE
function backprop(net: Net, x: number[], y: number[], act: ActName) {
  const { a, z } = forward(net, x, act);
  const L = net.layerSizes.length - 1; // last layer index in W/b

  // output error derivative dL/da = (a - y)
  let delta: number[] = a[L + 0].map((av, j) => (av - (y[j] ?? 0)) * dActivate(z[L - 1][j], a[L][j], act));
  const nablaB: number[][] = Array.from({ length: L }, (_, l) => new Array(net.layerSizes[l + 1]).fill(0));
  const nablaW: number[][][] = Array.from({ length: L }, (_, l) =>
    Array.from({ length: net.layerSizes[l] }, () => new Array(net.layerSizes[l + 1]).fill(0))
  );

  nablaB[L - 1] = delta.slice();
  for (let i = 0; i < net.layerSizes[L - 1]; i++)
    for (let j = 0; j < net.layerSizes[L]; j++)
      nablaW[L - 1][i][j] = a[L - 1][i] * delta[j];

  // backpropagate delta
  for (let l = L - 2; l >= 0; l--) {
    const newDelta: number[] = new Array(net.layerSizes[l + 1]).fill(0);
    for (let j = 0; j < net.layerSizes[l + 1]; j++) {
      let s = 0;
      for (let k = 0; k < net.layerSizes[l + 2]; k++) s += net.W[l + 1][j][k] * delta[k];
      newDelta[j] = s * dActivate(z[l][j], a[l + 1][j], act);
    }
    delta = newDelta;
    nablaB[l] = delta.slice();
    for (let i = 0; i < net.layerSizes[l]; i++)
      for (let j = 0; j < net.layerSizes[l + 1]; j++)
        nablaW[l][i][j] = a[l][i] * delta[j];
  }

  // loss
  const mse = a[L].reduce((acc, v, j) => acc + (v - (y[j] ?? 0)) ** 2, 0) / a[L].length;
  return { nablaW, nablaB, a, z, loss: mse };
}

// apply gradients
function applyGrads(net: Net, dW: number[][][], dB: number[][], lr: number) {
  for (let l = 0; l < net.W.length; l++) {
    for (let i = 0; i < net.W[l].length; i++) {
      for (let j = 0; j < net.W[l][i].length; j++) {
        net.W[l][i][j] -= lr * dW[l][i][j];
      }
    }
    for (let j = 0; j < net.b[l].length; j++) net.b[l][j] -= lr * dB[l][j];
  }
}

// =========================
// Demo dataset (XOR-esque)
// =========================
const DATA: { x: number[]; y: number[] }[] = [
  { x: [0, 0], y: [0] },
  { x: [0, 1], y: [1] },
  { x: [1, 0], y: [1] },
  { x: [1, 1], y: [0] },
];

// =========================
// Backprop animation: we push pulses along edges from output -> input
// =========================
interface Pulse { from: {x:number;y:number}; to:{x:number;y:number}; t:number; duration:number; }

// =========================
// Main component
// =========================
export default function BackpropagationVisualizer() {
  const [activation, setActivation] = useState<ActName>("sigmoid");
  const [architecture, setArchitecture] = useState<string>("2,3,3,1");
  const layerSizes = useMemo(() => architecture.split(",").map(s => Math.max(1, parseInt(s.trim()||"0",10))).filter(n=>!Number.isNaN(n)), [architecture]);

  const [net, setNet] = useState<Net>(() => initNet([2,3,2,1]));
  const [lr, setLr] = useState(0.01);
  const [sampleIdx, setSampleIdx] = useState(0);
  const [{ loss, activations }, setStats] = useState<{loss:number; activations:number[][]}>({loss:0, activations: []});
  const [isTraining, setIsTraining] = useState(false);

  const svgRef = useRef<SVGSVGElement | null>(null);
  const pulsesRef = useRef<Pulse[]>([]);
  const rafRef = useRef<number | null>(null);

  // Re-init network when architecture changes
  useEffect(() => { setNet(initNet(layerSizes)); }, [architecture]);

  // forward pass to show activations of current sample
  useEffect(() => {
    const { a } = forward(net, DATA[sampleIdx].x, activation);
    setStats(s => ({ ...s, activations: a, loss: s.loss }));
  }, [net, sampleIdx, activation]);

  // training loop (optional autoplay)
  useEffect(() => {
    if (!isTraining) return;

    let running = true;

    const trainLoop = () => {
      if (!running) return;

      const stepsPerTick = 10; // SGD steps per frame
      let lastActivations = activations;
      let lastLoss = loss;

      for (let step = 0; step < stepsPerTick; step++) {
        const datum = DATA[Math.floor(Math.random() * DATA.length)];
        const { nablaW, nablaB, a, z, loss: stepLoss } = backprop(net, datum.x, datum.y, activation);

        // update net in place
        applyGrads(net, nablaW, nablaB, lr);

        lastActivations = a;
        lastLoss = stepLoss;
      }

      // Update React state once per tick
      setStats({ loss: lastLoss, activations: lastActivations });

      // Add pulses for animation separately
      addBackpropPulses(lastActivations);

      rafRef.current = requestAnimationFrame(trainLoop);
    };

    trainLoop();

    return () => { running = false; if (rafRef.current) cancelAnimationFrame(rafRef.current); };
  }, [isTraining, lr, activation, net]);



  // add pulses from last forward pass (simulate gradients flowing back)
  function addBackpropPulses(a: number[][]) {
    const svg = svgRef.current; if (!svg) return;
    const nodes = computeLayout(layerSizes, svgWidth, svgHeight, 60);
    const pulses: Pulse[] = [];
    for (let l = layerSizes.length - 2; l >= 0; l--) {
      for (let i = 0; i < layerSizes[l]; i++) {
        for (let j = 0; j < layerSizes[l + 1]; j++) {
          pulses.push({ from: nodes[l + 1][j], to: nodes[l][i], t: 0, duration: 500 + 80 * (layerSizes.length - l) });
        }
      }
    }
    pulsesRef.current = pulses;
    animatePulses();
  }

  function animatePulses() {
    const svg = svgRef.current; if (!svg) return;
    const start = performance.now();

    function tick(now: number) {
      const t = now - start;
      let active = false;

      pulsesRef.current.forEach(p => {
        if (p.t < p.duration) {
          p.t = Math.min(p.duration, p.t + 16); // increment ~1 frame (16ms)
          active = true;
        }
      });

      renderPulses();

      if (active) requestAnimationFrame(tick); // continue animation
    }

    requestAnimationFrame(tick);
  }


  function renderPulses() {
    const svg = svgRef.current; if (!svg) return;
    svg.querySelectorAll('.pulse').forEach(n => n.remove());

    for (const p of pulsesRef.current) {
      const ratio = p.duration === 0 ? 1 : clamp01(p.t / p.duration);
      const x = lerp(p.from.x, p.to.x, ratio);
      const y = lerp(p.from.y, p.to.y, ratio);
      const circ = document.createElementNS("http://www.w3.org/2000/svg", "circle");
      circ.setAttribute("class", "pulse");
      circ.setAttribute("cx", String(x));
      circ.setAttribute("cy", String(y));
      circ.setAttribute("r", "4");
      circ.setAttribute("opacity", "0.3");        // subtle
      circ.setAttribute("fill", "#facc1590");     // very light yellow
      svg.appendChild(circ);
    }
  }

  function trainUntilStable(epsilon: number = 0.001, maxSteps: number = 10000) {
    let lastLoss = Infinity;
    let stepCount = 0;

    const stepLoop = () => {
      if (stepCount >= maxSteps) return; // stop after too many steps

      const datum = DATA[Math.floor(Math.random() * DATA.length)];
      const { nablaW, nablaB, a, z, loss } = backprop(net, datum.x, datum.y, activation);
      applyGrads(net, nablaW, nablaB, lr);

      setStats({ loss, activations: a });
      addBackpropPulses(a);

      stepCount++;

      // check stability: loss change < epsilon
      if (Math.abs(loss - lastLoss) < epsilon) {
        console.log(`Network stabilized at step ${stepCount}, loss=${loss.toFixed(4)}`);
        return;
      }

      lastLoss = loss;
      setTimeout(stepLoop, 10); // next step
    };

    stepLoop();
  }


  



  const svgWidth = 760;
  const svgHeight = 320;
  const nodeRadius = 16;

  const layout = useMemo(() => computeLayout(layerSizes, svgWidth, svgHeight, nodeRadius * 3), [layerSizes]);

  // controls
  function trainOne() {
    const datum = DATA[sampleIdx];
    const { nablaW, nablaB, a, z, loss } = backprop(net, datum.x, datum.y, activation);
    addBackpropPulses(a);
    const clone: Net = { layerSizes: net.layerSizes.slice(), W: net.W.map(r => r.map(c => c.slice())), b: net.b.map(v => v.slice()) };
    applyGrads(clone, nablaW, nablaB, lr);
    setNet(clone);
    setStats({ loss, activations: a });
  }

  function resetNet() { setNet(initNet(layerSizes)); setStats(s=>({...s,loss:0})); }

  // normalize activation to [0,1] for color
  function actTo01(v: number) {
    if (activation === "sigmoid") return v;
    if (activation === "tanh") return (v + 1) / 2;
    // relu: assume 0..1 display
    return clamp01(v);
  }

  return (
    <div className="min-h-screen bg-slate-100 p-6 flex flex-col items-center gap-6">
      <h1 className="text-4xl font-extrabold tracking-tight text-slate-900">Backpropagation Visualizer</h1>

      {/* Controls */}
      <div className="w-full max-w-5xl grid md:grid-cols-3 gap-4">
        <div className="md:col-span-2 bg-white rounded-2xl shadow p-4">
          <h2 className="text-lg font-semibold text-slate-800 mb-3">Network Settings</h2>
          <div className="flex flex-wrap gap-3 items-center mb-3">
            <label className="text-slate-700">Activation
              <select value={activation} onChange={e=>setActivation(e.target.value as ActName)}
                      className="ml-2 px-2 py-1 rounded border bg-slate-50">
                <option value="sigmoid">Sigmoid</option>
                <option value="relu">ReLU</option>
                <option value="tanh">Tanh</option>
              </select>
            </label>
            <label className="text-slate-700">Architecture
              <input value={architecture} onChange={e=>setArchitecture(e.target.value)}
                     className="ml-2 px-2 py-1 rounded border bg-slate-50 w-36" />
            </label>
            <label className="text-slate-700">Learning rate
              <input type="range" min="0.01" max="1" step="0.01" value={lr}
                     onChange={e=>setLr(parseFloat(e.target.value))}
                     className="ml-2 align-middle" />
              <span className="ml-2 text-sm text-slate-600">{lr.toFixed(2)}</span>
            </label>
            <label className="text-slate-700">Sample
              <select value={sampleIdx} onChange={e=>setSampleIdx(parseInt(e.target.value))}
                      className="ml-2 px-2 py-1 rounded border bg-slate-50">
                {DATA.map((d, i) => (
                  <option key={i} value={i}>{`x=[${d.x.join(", ")}] → y=[${d.y.join(", ")}]`}</option>
                ))}
              </select>
            </label>
          </div>
          <div className="flex gap-3">
            <button onClick={trainOne} className="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 shadow">Train 1 Step</button>
            <button onClick={() => trainUntilStable(0.001,10000)} className="px-4 py-2 rounded-lg bg-emerald-600 text-white hover:bg-emerald-700 shadow"> Train Until Converged </button>
            <button onClick={resetNet} className="px-4 py-2 rounded-lg bg-red-500 text-white hover:bg-red-600 shadow">Reset Weights</button>
          </div>
        </div>
        <div className="bg-white rounded-2xl shadow p-4">
          <h3 className="font-semibold text-slate-800 mb-2">Stats</h3>
          <div className="text-sm text-slate-700 space-y-1">
            <p><span className="font-medium">Loss:</span> {loss.toFixed(4)}</p>
            <p><span className="font-medium">Layers:</span> [{layerSizes.join(", ")}]</p>
            <p className="text-slate-500">Tip: Edit architecture as comma-separated sizes (e.g., 2,4,4,1) and it will re-init.</p>
          </div>
        </div>
      </div>

      {/* Visualization */}
      <div className="w-full max-w-5xl bg-white rounded-2xl shadow p-4">
        <h2 className="text-lg font-semibold text-slate-800 mb-3">Forward & Backward Pass</h2>
        <svg ref={svgRef} width={svgWidth} height={svgHeight} className="w-full max-w-full">
          {/* Edges */}
          {net.W.map((wLayer, l) => (
            <g key={`edges-${l}`}>
              {wLayer.map((row, i) => row.map((w, j) => {
                const from = layout[l][i];
                const to = layout[l+1][j];
                const width = 1 + Math.min(4, Math.abs(w) * 6);
                const color = weightColor(Math.max(-1, Math.min(1, w)));
                return (
                  <line key={`e-${l}-${i}-${j}`} x1={from.x} y1={from.y} x2={to.x} y2={to.y}
                        stroke={color} strokeWidth={width} opacity={0.9} />
                );
              }))}
            </g>
          ))}

          {/* Nodes */}
          {layout.map((layer, li) => (
            <g key={`layer-${li}`}>
              {layer.map((pt, ni) => {
                const aVal = activations?.[li]?.[ni] ?? (li===0 ? DATA[sampleIdx].x[ni] : 0);
        
                return (
                  <g key={`n-${li}-${ni}`}>
                    <circle cx={pt.x} cy={pt.y} r={nodeRadius} fill="white" stroke="#0f172a" strokeWidth={1.5} />
                    <text x={pt.x} y={pt.y+4} textAnchor="middle" fontSize={12} fontWeight="500" stroke="none" strokeWidth={0} >{aVal.toFixed(2)}</text>
                  </g>
                );
              })}
            </g>
          ))}
        </svg>
        <p className="text-xs text-slate-500 mt-2">Edge color: blue=positive, violet=negative; thickness ∝ |weight|.</p>
      </div>
    </div>
  );
}

// =========================
// Layout helper: compute (x,y) for each node in each layer
// =========================
function computeLayout(layerSizes: number[], width: number, height: number, xGap: number) {
  const marginX = 40;
  const marginY = 24;
  const totalLayers = layerSizes.length;
  const usableW = Math.max(1, width - marginX * 2);
  const layerGap = usableW / Math.max(1, totalLayers - 1);

  const layers: { x: number; y: number }[][] = [];
  for (let l = 0; l < totalLayers; l++) {
    const n = layerSizes[l];
    const x = marginX + l * layerGap;
    const totalH = height - marginY * 2;
    const step = totalH / (n + 1);
    const pts = Array.from({ length: n }, (_, i) => ({ x, y: marginY + (i + 1) * step }));
    layers.push(pts);
  }
  return layers;
}
