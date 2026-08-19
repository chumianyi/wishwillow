import * as THREE from './js/three.module.js';
import { EffectComposer } from './js/postprocessing/EffectComposer.js';
import { RenderPass } from './js/postprocessing/RenderPass.js';
import { UnrealBloomPass } from './js/postprocessing/UnrealBloomPass.js';

// ============ Audio ============
const ambientAudio = new Audio('ambient.wav');
ambientAudio.loop = true;
ambientAudio.volume = 0.35;
document.addEventListener('touchstart', () => { if (ambientAudio.paused) ambientAudio.play().catch(()=>{}); }, { once: true });
document.addEventListener('click', () => { if (ambientAudio.paused) ambientAudio.play().catch(()=>{}); }, { once: true });

// ============ Scene Setup ============
const container = document.getElementById('canvas-container');
const scene = new THREE.Scene();
scene.background = new THREE.Color(0xE0E5EC);
scene.fog = new THREE.Fog(0xE0E5EC, 15, 40);

const camera = new THREE.PerspectiveCamera(50, window.innerWidth / window.innerHeight, 0.1, 100);
camera.position.set(0, 3, 10);
camera.lookAt(0, 3, 0);

const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
renderer.setSize(window.innerWidth, window.innerHeight);
renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
renderer.shadowMap.enabled = true;
renderer.shadowMap.type = THREE.PCFSoftShadowMap;
renderer.toneMapping = THREE.ACESFilmicToneMapping;
renderer.toneMappingExposure = 1.0;
container.appendChild(renderer.domElement);

// ============ Textures ============
const textureLoader = new THREE.TextureLoader();
const envTex = textureLoader.load('environment.jpg');
envTex.mapping = THREE.EquirectangularReflectionMapping;
envTex.colorSpace = THREE.SRGBColorSpace;
scene.background = envTex;
scene.environment = envTex;

const barkTex = textureLoader.load('bark.jpg');
barkTex.wrapS = barkTex.wrapT = THREE.RepeatWrapping;
barkTex.repeat.set(2, 4);

const groundTex = textureLoader.load('ground_4k.jpg');
groundTex.wrapS = groundTex.wrapT = THREE.RepeatWrapping;
groundTex.repeat.set(8, 8);

// ============ Post-processing (Bloom) ============
const composer = new EffectComposer(renderer);
const renderPass = new RenderPass(scene, camera);
composer.addPass(renderPass);
const bloomPass = new UnrealBloomPass(
  new THREE.Vector2(window.innerWidth, window.innerHeight),
  0.3, 0.4, 0.85
);
composer.addPass(bloomPass);

// ============ Lights ============
const ambientLight = new THREE.AmbientLight(0xffffff, 0.6);
scene.add(ambientLight);

const dirLight = new THREE.DirectionalLight(0xfff5e6, 0.9);
dirLight.position.set(5, 10, 5);
dirLight.castShadow = true;
dirLight.shadow.mapSize.width = 2048;
dirLight.shadow.mapSize.height = 2048;
dirLight.shadow.camera.near = 0.5;
dirLight.shadow.camera.far = 30;
dirLight.shadow.camera.left = -8;
dirLight.shadow.camera.right = 8;
dirLight.shadow.camera.top = 12;
dirLight.shadow.camera.bottom = -2;
scene.add(dirLight);

const fillLight = new THREE.DirectionalLight(0xc8d8f0, 0.3);
fillLight.position.set(-5, 5, -3);
scene.add(fillLight);

// Result point light (initially hidden)
const resultLight = new THREE.PointLight(0xffffff, 0, 15, 2);
resultLight.position.set(0, 4, 0);
scene.add(resultLight);

// Glow sphere at break point
const glowGeo = new THREE.SphereGeometry(0.15, 16, 16);
const glowMat = new THREE.MeshBasicMaterial({ color: 0xffffff, transparent: true, opacity: 0 });
const glowSphere = new THREE.Mesh(glowGeo, glowMat);
glowSphere.position.set(0, 4.5, 0);
scene.add(glowSphere);

// ============ Ground ============
const groundGeo = new THREE.PlaneGeometry(40, 40);
const groundMat = new THREE.MeshStandardMaterial({
  color: 0xffffff, map: groundTex, roughness: 0.95, metalness: 0.0
});
const ground = new THREE.Mesh(groundGeo, groundMat);
ground.rotation.x = -Math.PI / 2;
ground.receiveShadow = true;
scene.add(ground);

// ============ Willow Tree ============
const treeGroup = new THREE.Group();
scene.add(treeGroup);

// Trunk - curved using a custom geometry
function createTrunk() {
  const trunkHeight = 4;
  const trunkSegments = 20;
  const trunkGeo = new THREE.CylinderGeometry(0.25, 0.45, trunkHeight, 12, trunkSegments);
  
  // Bend the trunk
  const positions = trunkGeo.attributes.position;
  for (let i = 0; i < positions.count; i++) {
    const y = positions.getY(i);
    const t = (y + trunkHeight / 2) / trunkHeight;
    const bendX = Math.sin(t * Math.PI * 0.5) * 0.3;
    const bendZ = Math.cos(t * Math.PI * 0.3) * 0.15;
    positions.setX(i, positions.getX(i) + bendX);
    positions.setZ(i, positions.getZ(i) + bendZ);
  }
  trunkGeo.computeVertexNormals();
  
  const trunkMat = new THREE.MeshStandardMaterial({
    color: 0xffffff, map: barkTex, roughness: 0.85, metalness: 0.05
  });
  const trunk = new THREE.Mesh(trunkGeo, trunkMat);
  trunk.position.y = trunkHeight / 2;
  trunk.castShadow = true;
  trunk.receiveShadow = true;
  return trunk;
}

const trunk = createTrunk();
treeGroup.add(trunk);

// Branches (main branches from top of trunk)
const branches = [];
const branchCurves = [];

function createBranch(startPos, direction, length, thickness, depth) {
  if (depth <= 0 || length < 0.3) return null;
  
  const points = [];
  const segments = 8;
  const endPos = startPos.clone().add(direction.clone().multiplyScalar(length));
  
  // Add some curve
  const midPos = startPos.clone().lerp(endPos, 0.5);
  midPos.x += (Math.random() - 0.5) * 0.5;
  midPos.z += (Math.random() - 0.5) * 0.5;
  midPos.y += length * 0.1;
  
  const curve = new THREE.QuadraticBezierCurve3(startPos, midPos, endPos);
  branchCurves.push({ curve, thickness, depth });
  
  for (let i = 0; i <= segments; i++) {
    points.push(curve.getPoint(i / segments));
  }
  
  const tubeGeo = new THREE.TubeGeometry(new THREE.CatmullRomCurve3(points), segments, thickness, 6, false);
  const branchMat = new THREE.MeshStandardMaterial({
    color: 0xffffff, map: barkTex, roughness: 0.8, metalness: 0.05
  });
  const branch = new THREE.Mesh(tubeGeo, branchMat);
  branch.castShadow = true;
  branch.receiveShadow = true;
  branches.push(branch);
  treeGroup.add(branch);
  
  // Sub-branches
  if (depth > 1) {
    const numSub = Math.floor(Math.random() * 2) + 1;
    for (let i = 0; i < numSub; i++) {
      const t = 0.4 + Math.random() * 0.5;
      const subStart = curve.getPoint(t);
      const subDir = new THREE.Vector3(
        (Math.random() - 0.5) * 1.5,
        Math.random() * 0.5 - 0.2,
        (Math.random() - 0.5) * 1.5
      ).normalize();
      createBranch(subStart, subDir, length * 0.55, thickness * 0.6, depth - 1);
    }
  }
  
  return branch;
}

// Create main branches from trunk top
const trunkTop = new THREE.Vector3(0.3, 4, 0.15);
const numMainBranches = 5;
for (let i = 0; i < numMainBranches; i++) {
  const angle = (i / numMainBranches) * Math.PI * 2 + Math.random() * 0.3;
  const dir = new THREE.Vector3(
    Math.cos(angle) * 0.6,
    0.5 + Math.random() * 0.3,
    Math.sin(angle) * 0.6
  ).normalize();
  createBranch(trunkTop.clone(), dir, 2.5 + Math.random() * 1, 0.12, 3);
}

// ============ Drooping Willow Twigs (the iconic weeping branches) ============
const twigs = [];
const twigData = []; // Store original positions for animation

function createDroopingTwig(startPos, length) {
  const segments = 12;
  const points = [];
  
  // Create a drooping curve
  for (let i = 0; i <= segments; i++) {
    const t = i / segments;
    const x = startPos.x + (Math.random() - 0.5) * 0.1;
    const y = startPos.y - length * t + Math.sin(t * Math.PI) * 0.3;
    const z = startPos.z + (Math.random() - 0.5) * 0.1;
    points.push(new THREE.Vector3(x, y, z));
  }
  
  const curve = new THREE.CatmullRomCurve3(points);
  const twigGeo = new THREE.TubeGeometry(curve, segments, 0.02, 4, false);
  const twigMat = new THREE.MeshStandardMaterial({
    color: 0x7a9a5a, roughness: 0.7, metalness: 0.0
  });
  const twig = new THREE.Mesh(twigGeo, twigMat);
  twig.castShadow = true;
  
  // Store original positions for sway animation
  const origPositions = [];
  const posAttr = twigGeo.attributes.position;
  for (let i = 0; i < posAttr.count; i++) {
    origPositions.push(new THREE.Vector3(
      posAttr.getX(i), posAttr.getY(i), posAttr.getZ(i)
    ));
  }
  
  twigs.push(twig);
  twigData.push({ mesh: twig, origPositions, phase: Math.random() * Math.PI * 2, curve });
  treeGroup.add(twig);
  
  // Add leaves along the twig
  createLeavesOnTwig(curve, length);
  
  return twig;
}

// Leaves
const leavesGroup = new THREE.Group();
treeGroup.add(leavesGroup);
const allLeaves = [];

function createLeavesOnTwig(curve, length) {
  const numLeaves = Math.floor(length * 4);
  const leafGeo = new THREE.PlaneGeometry(0.08, 0.15);
  const leafMat = new THREE.MeshStandardMaterial({
    color: 0x6ab04c, roughness: 0.6, metalness: 0.0,
    side: THREE.DoubleSide
  });
  
  for (let i = 0; i < numLeaves; i++) {
    const t = 0.2 + (i / numLeaves) * 0.8;
    const pos = curve.getPoint(t);
    const leaf = new THREE.Mesh(leafGeo, leafMat.clone());
    leaf.position.copy(pos);
    leaf.rotation.set(
      Math.random() * Math.PI,
      Math.random() * Math.PI,
      Math.random() * Math.PI
    );
    leaf.material.color.setHSL(0.28 + Math.random() * 0.08, 0.5, 0.4 + Math.random() * 0.15);
    leaf.castShadow = true;
    leaf.userData.basePos = pos.clone();
    leaf.userData.phase = Math.random() * Math.PI * 2;
    leavesGroup.add(leaf);
    allLeaves.push(leaf);
  }
}

// Create drooping twigs from branch ends
function addDroopingTwigs() {
  // From main branch endpoints and midpoints
  branchCurves.forEach(({ curve, depth }) => {
    if (depth >= 2) {
      const numTwigs = depth === 3 ? 3 : 2;
      for (let i = 0; i < numTwigs; i++) {
        const t = 0.5 + (i / numTwigs) * 0.5;
        const startPos = curve.getPoint(t);
        const length = 1.5 + Math.random() * 2;
        createDroopingTwig(startPos.clone(), length);
      }
    }
  });
  
  // Also add some from trunk top area
  for (let i = 0; i < 4; i++) {
    const angle = (i / 4) * Math.PI * 2;
    const startPos = new THREE.Vector3(
      trunkTop.x + Math.cos(angle) * 0.5,
      trunkTop.y - 0.2,
      trunkTop.z + Math.sin(angle) * 0.5
    );
    createDroopingTwig(startPos, 2 + Math.random() * 1.5);
  }
}

addDroopingTwigs();

// ============ The "Wish Branch" - special branch that gets broken ============
// This is a prominent branch that the hand will grab and break
const wishBranchGroup = new THREE.Group();
treeGroup.add(wishBranchGroup);

const wishBranchStart = new THREE.Vector3(0.5, 3.8, 0.2);
const wishBranchLength = 2.2;
let wishBranchCurve, wishBranchMesh, wishBranchOrigPositions;
let wishBranchBroken = false;
let wishBreakPoint = null;
let brokenTopPart = null;

function createWishBranch() {
  const segments = 16;
  const points = [];
  
  // A branch that extends outward and slightly upward, then droops at the end
  for (let i = 0; i <= segments; i++) {
    const t = i / segments;
    const x = wishBranchStart.x + t * wishBranchLength * 0.8;
    const y = wishBranchStart.y + Math.sin(t * Math.PI) * 0.8 - t * 0.3;
    const z = wishBranchStart.z + t * 0.3;
    points.push(new THREE.Vector3(x, y, z));
  }
  
  wishBranchCurve = new THREE.CatmullRomCurve3(points);
  const geo = new THREE.TubeGeometry(wishBranchCurve, segments, 0.08, 8, false);
  const mat = new THREE.MeshStandardMaterial({
    color: 0xffffff, map: barkTex, roughness: 0.75, metalness: 0.05
  });
  wishBranchMesh = new THREE.Mesh(geo, mat);
  wishBranchMesh.castShadow = true;
  wishBranchMesh.receiveShadow = true;
  
  // Store original positions
  wishBranchOrigPositions = [];
  const posAttr = geo.attributes.position;
  for (let i = 0; i < posAttr.count; i++) {
    wishBranchOrigPositions.push(new THREE.Vector3(
      posAttr.getX(i), posAttr.getY(i), posAttr.getZ(i)
    ));
  }
  
  // Add leaves to wish branch
  const leafGeo = new THREE.PlaneGeometry(0.1, 0.18);
  for (let i = 0; i < 15; i++) {
    const t = 0.2 + (i / 15) * 0.7;
    const pos = wishBranchCurve.getPoint(t);
    const leaf = new THREE.Mesh(leafGeo, new THREE.MeshStandardMaterial({
      color: new THREE.Color().setHSL(0.3 + Math.random() * 0.05, 0.55, 0.45),
      roughness: 0.6, side: THREE.DoubleSide
    }));
    leaf.position.copy(pos);
    leaf.rotation.set(Math.random() * Math.PI, Math.random() * Math.PI, Math.random() * Math.PI);
    leaf.castShadow = true;
    leaf.userData.basePos = pos.clone();
    leaf.userData.phase = Math.random() * Math.PI * 2;
    wishBranchGroup.add(leaf);
    allLeaves.push(leaf);
  }
  
  wishBranchGroup.add(wishBranchMesh);
  
  // The break point (about 60% along the branch)
  wishBreakPoint = wishBranchCurve.getPoint(0.6);
}

createWishBranch();

// ============ Hand Model ============
const handGroup = new THREE.Group();
handGroup.visible = false;
scene.add(handGroup);

// Hand starts hidden behind the tree, will animate out
const handBasePos = new THREE.Vector3(-0.5, 3.5, 0.5);
handGroup.position.copy(handBasePos);

function createHand() {
  const skinMat = new THREE.MeshStandardMaterial({
    color: 0xd4a574, roughness: 0.6, metalness: 0.05
  });
  
  // Palm
  const palmGeo = new THREE.BoxGeometry(0.35, 0.4, 0.12);
  const palm = new THREE.Mesh(palmGeo, skinMat);
  palm.castShadow = true;
  handGroup.add(palm);
  
  // Fingers
  const fingerGeo = new THREE.CylinderGeometry(0.035, 0.03, 0.3, 8);
  const fingerPositions = [
    [-0.12, 0.3, 0], [-0.04, 0.32, 0], [0.04, 0.32, 0], [0.12, 0.28, 0]
  ];
  fingerPositions.forEach((pos, i) => {
    const finger = new THREE.Mesh(fingerGeo, skinMat);
    finger.position.set(pos[0], pos[1], pos[2]);
    finger.castShadow = true;
    finger.userData.baseY = pos[1];
    finger.userData.index = i;
    handGroup.add(finger);
  });
  
  // Thumb
  const thumbGeo = new THREE.CylinderGeometry(0.04, 0.035, 0.25, 8);
  const thumb = new THREE.Mesh(thumbGeo, skinMat);
  thumb.position.set(-0.2, 0.1, 0.02);
  thumb.rotation.z = Math.PI / 3;
  thumb.castShadow = true;
  handGroup.add(thumb);
  
  // Arm (branch-like)
  const armGeo = new THREE.CylinderGeometry(0.08, 0.12, 1.5, 8);
  const armMat = new THREE.MeshStandardMaterial({
    color: 0x6b4423, roughness: 0.8
  });
  const arm = new THREE.Mesh(armGeo, armMat);
  arm.position.set(0, -0.8, 0);
  arm.castShadow = true;
  handGroup.add(arm);
}

createHand();

// ============ Particle System ============
const particleCount = 200;
const particleGeo = new THREE.BufferGeometry();
const particlePositions = new Float32Array(particleCount * 3);
const particleVelocities = [];
const particleColors = new Float32Array(particleCount * 3);
const particleSizes = new Float32Array(particleCount);
const particleLifetimes = new Float32Array(particleCount);

for (let i = 0; i < particleCount; i++) {
  particlePositions[i * 3] = 0;
  particlePositions[i * 3 + 1] = -100;
  particlePositions[i * 3 + 2] = 0;
  particleVelocities.push(new THREE.Vector3());
  particleLifetimes[i] = 0;
  particleSizes[i] = Math.random() * 0.05 + 0.02;
}

particleGeo.setAttribute('position', new THREE.BufferAttribute(particlePositions, 3));
particleGeo.setAttribute('color', new THREE.BufferAttribute(particleColors, 3));
particleGeo.setAttribute('size', new THREE.BufferAttribute(particleSizes, 1));

const particleMat = new THREE.PointsMaterial({
  size: 0.08, vertexColors: true, transparent: true,
  opacity: 0.9, blending: THREE.AdditiveBlending, depthWrite: false
});
const particles = new THREE.Points(particleGeo, particleMat);
scene.add(particles);

let activeParticles = 0;

function emitParticles(position, color, count, speed) {
  for (let i = 0; i < count && activeParticles < particleCount; i++) {
    const idx = activeParticles % particleCount;
    particlePositions[idx * 3] = position.x;
    particlePositions[idx * 3 + 1] = position.y;
    particlePositions[idx * 3 + 2] = position.z;
    
    const theta = Math.random() * Math.PI * 2;
    const phi = Math.random() * Math.PI;
    const v = speed * (0.5 + Math.random() * 0.5);
    particleVelocities[idx].set(
      Math.sin(phi) * Math.cos(theta) * v,
      Math.cos(phi) * v + 1,
      Math.sin(phi) * Math.sin(theta) * v
    );
    
    particleColors[idx * 3] = color.r;
    particleColors[idx * 3 + 1] = color.g;
    particleColors[idx * 3 + 2] = color.b;
    
    particleLifetimes[idx] = 1.0 + Math.random() * 1.5;
    activeParticles++;
  }
  particleGeo.attributes.position.needsUpdate = true;
  particleGeo.attributes.color.needsUpdate = true;
}

function updateParticles(delta) {
  for (let i = 0; i < particleCount; i++) {
    if (particleLifetimes[i] > 0) {
      particleLifetimes[i] -= delta;
      if (particleLifetimes[i] <= 0) {
        particlePositions[i * 3 + 1] = -100;
        continue;
      }
      
      particleVelocities[i].y -= 3 * delta; // gravity
      particlePositions[i * 3] += particleVelocities[i].x * delta;
      particlePositions[i * 3 + 1] += particleVelocities[i].y * delta;
      particlePositions[i * 3 + 2] += particleVelocities[i].z * delta;
    }
  }
  particleGeo.attributes.position.needsUpdate = true;
}

// ============ Animation State ============
let gameState = 'idle'; // idle, extending, grabbing, breaking, result
let animTime = 0;
let resultType = null; // 'white', 'red', 'fail'
let branchBendAmount = 0;
let handTargetPos = handBasePos.clone();
let bloomTarget = 0.3;
let lightTargetIntensity = 0;

const costTexts = [
  '三年寿命', '一段记忆', '一次好运', '一缕头发',
  '一滴眼泪', '一个秘密', '一段友情', '一次机会',
  '一寸光阴', '一份勇气'
];

// ============ Wish Branch Animation ============
function updateWishBranch(time) {
  if (!wishBranchMesh || wishBranchBroken) return;
  
  const geo = wishBranchMesh.geometry;
  const posAttr = geo.attributes.position;
  const bend = branchBendAmount;
  
  for (let i = 0; i < posAttr.count; i++) {
    const orig = wishBranchOrigPositions[i];
    const t = i / (posAttr.count - 1);
    
    // Apply bending - the further along, the more it bends down
    const bendFactor = Math.pow(t, 1.5) * bend;
    const swayX = Math.sin(time * 1.5 + t * 3) * 0.02 * (1 - bend * 0.5);
    const swayZ = Math.cos(time * 1.2 + t * 2) * 0.015;
    
    posAttr.setX(i, orig.x + swayX);
    posAttr.setY(i, orig.y - bendFactor * 1.5 + swayX * 0.5);
    posAttr.setZ(i, orig.z + swayZ);
  }
  posAttr.needsUpdate = true;
  geo.computeVertexNormals();
}

// ============ Twig Sway Animation ============
function updateTwigs(time) {
  twigData.forEach(({ mesh, origPositions, phase }) => {
    const geo = mesh.geometry;
    const posAttr = geo.attributes.position;
    
    for (let i = 0; i < posAttr.count; i++) {
      const orig = origPositions[i];
      const t = i / (posAttr.count - 1);
      const swayAmount = t * 0.08;
      const swayX = Math.sin(time * 1.2 + phase + t * 4) * swayAmount;
      const swayZ = Math.cos(time * 1.0 + phase + t * 3) * swayAmount * 0.6;
      
      posAttr.setX(i, orig.x + swayX);
      posAttr.setZ(i, orig.z + swayZ);
    }
    posAttr.needsUpdate = true;
  });
  
  // Leaf sway
  allLeaves.forEach(leaf => {
    const base = leaf.userData.basePos;
    const phase = leaf.userData.phase;
    leaf.position.x = base.x + Math.sin(time * 2 + phase) * 0.015;
    leaf.position.z = base.z + Math.cos(time * 1.5 + phase) * 0.01;
    leaf.rotation.z += Math.sin(time + phase) * 0.002;
  });
}

// ============ Hand Animation ============
function updateHand(delta) {
  handGroup.position.lerp(handTargetPos, 0.08);
  
  // Finger curl animation when grabbing
  const fingers = handGroup.children.filter(c => c.userData.index !== undefined);
  fingers.forEach(finger => {
    const targetRot = gameState === 'grabbing' || gameState === 'breaking' ? -0.8 : 0;
    finger.rotation.x += (targetRot - finger.rotation.x) * 0.1;
  });
}

// ============ Break Branch ============
function breakBranch(type) {
  wishBranchBroken = true;
  
  // Create broken top part
  const breakT = 0.6;
  const segments = 16;
  const topPoints = [];
  
  for (let i = Math.floor(segments * breakT); i <= segments; i++) {
    const t = i / segments;
    topPoints.push(wishBranchCurve.getPoint(t).clone());
  }
  
  const topCurve = new THREE.CatmullRomCurve3(topPoints);
  const topGeo = new THREE.TubeGeometry(topCurve, topPoints.length - 1, 0.08, 8, false);
  const topMat = new THREE.MeshStandardMaterial({
    color: 0x6b4423, roughness: 0.75
  });
  brokenTopPart = new THREE.Mesh(topGeo, topMat);
  brokenTopPart.castShadow = true;
  brokenTopPart.position.copy(wishBreakPoint);
  brokenTopPart.userData.velocity = new THREE.Vector3(
    (Math.random() - 0.3) * 2, 1, (Math.random() - 0.5) * 1.5
  );
  brokenTopPart.userData.angularVel = new THREE.Vector3(
    Math.random() * 2, Math.random() * 2, Math.random() * 2
  );
  scene.add(brokenTopPart);
  
  // Hide original wish branch (it's now broken)
  wishBranchMesh.visible = false;
  
  // Emit particles
  const particleColor = type === 'white' ? new THREE.Color(0xffffff) :
                        type === 'red' ? new THREE.Color(0xff3030) :
                        new THREE.Color(0x8b7355);
  emitParticles(wishBreakPoint, particleColor, 80, 3);
  
  // Emit wood chips
  emitParticles(wishBreakPoint, new THREE.Color(0x8b6914), 30, 2);
  
  // Set glow
  if (type !== 'fail') {
    glowSphere.position.copy(wishBreakPoint);
    glowMat.color.setHex(type === 'white' ? 0xffffff : 0xff2020);
    resultLight.color.setHex(type === 'white' ? 0xffffff : 0xff2020);
    lightTargetIntensity = 5;
    bloomTarget = 1.5;
  }
}

// ============ Game Flow ============
function startWish() {
  if (gameState !== 'idle') return;
  
  const wishText = document.getElementById('wishInput').value.trim();
  if (!wishText) {
    // Shake input
    const input = document.getElementById('wishInput');
    input.style.animation = 'none';
    setTimeout(() => { input.style.animation = 'shake 0.4s'; }, 10);
    return;
  }
  
  gameState = 'extending';
  animTime = 0;
  handGroup.visible = true;
  handTargetPos.copy(handBasePos);
  
  // Hide wish panel
  document.getElementById('wishPanel').classList.add('hidden');
  
  // Determine result (but don't reveal yet)
  const rand = Math.random();
  if (rand < 0.4) resultType = 'white';
  else if (rand < 0.75) resultType = 'red';
  else resultType = 'fail';
}

function showResult() {
  const panel = document.getElementById('resultPanel');
  const title = document.getElementById('resultTitle');
  const cost = document.getElementById('resultCost');
  
  title.className = 'result-title';
  
  if (resultType === 'white') {
    title.textContent = '许愿成功 · 无代价';
    title.classList.add('white');
    cost.textContent = '柳枝沐浴在圣洁的白光中，你的愿望已被聆听。';
    cost.className = 'result-cost';
  } else if (resultType === 'red') {
    title.textContent = '许愿成功 · 需付出代价';
    title.classList.add('red');
    const randomCost = costTexts[Math.floor(Math.random() * costTexts.length)];
    cost.textContent = `代价：${randomCost}。暗红之光昭示着等价交换。`;
    cost.className = 'result-cost red-cost';
  } else {
    title.textContent = '许愿失败 · 柳枝未断';
    title.classList.add('fail');
    cost.textContent = '柳枝坚韧不屈，你的愿望尚未被接纳。再试一次吧。';
    cost.className = 'result-cost';
  }
  
  panel.classList.add('show');
}

function resetGame() {
  gameState = 'idle';
  animTime = 0;
  resultType = null;
  branchBendAmount = 0;
  wishBranchBroken = false;
  bloomTarget = 0.3;
  lightTargetIntensity = 0;
  resultLight.intensity = 0;
  glowMat.opacity = 0;
  
  // Restore wish branch
  if (wishBranchMesh) {
    wishBranchMesh.visible = true;
    const geo = wishBranchMesh.geometry;
    const posAttr = geo.attributes.position;
    for (let i = 0; i < posAttr.count; i++) {
      const orig = wishBranchOrigPositions[i];
      posAttr.setXYZ(i, orig.x, orig.y, orig.z);
    }
    posAttr.needsUpdate = true;
    geo.computeVertexNormals();
  }
  
  // Remove broken part
  if (brokenTopPart) {
    scene.remove(brokenTopPart);
    brokenTopPart = null;
  }
  
  // Hide hand
  handGroup.visible = false;
  handGroup.position.copy(handBasePos);
  handTargetPos.copy(handBasePos);
  
  // Reset UI
  document.getElementById('resultPanel').classList.remove('show');
  document.getElementById('wishPanel').classList.remove('hidden');
  document.getElementById('wishInput').value = '';
}

// ============ UI Events ============
document.getElementById('wishBtn').addEventListener('click', startWish);
document.getElementById('retryBtn').addEventListener('click', resetGame);
document.getElementById('wishInput').addEventListener('keydown', (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault();
    startWish();
  }
});

// ============ Main Animation Loop ============
const clock = new THREE.Clock();

function animate() {
  requestAnimationFrame(animate);
  const delta = Math.min(clock.getDelta(), 0.05);
  const time = clock.getElapsedTime();
  
  animTime += delta;
  
  // Update ambient animations
  updateTwigs(time);
  updateWishBranch(time);
  updateHand(delta);
  updateParticles(delta);
  
  // Game state machine
  if (gameState === 'extending') {
    // Hand reaches out toward the wish branch
    const grabPos = wishBreakPoint.clone();
    grabPos.x -= 0.3;
    grabPos.y += 0.1;
    handTargetPos.lerp(grabPos, 0.04);
    
    if (animTime > 1.5) {
      gameState = 'grabbing';
      animTime = 0;
    }
  } else if (gameState === 'grabbing') {
    // Hand grabs and starts bending the branch
    branchBendAmount = Math.min(branchBendAmount + delta * 0.8, 1.0);
    
    if (animTime > 1.2) {
      if (resultType === 'fail') {
        // Branch doesn't break, springs back
        gameState = 'springback';
        animTime = 0;
      } else {
        gameState = 'breaking';
        animTime = 0;
      }
    }
  } else if (gameState === 'breaking') {
    // Final bend then break
    branchBendAmount = Math.min(branchBendAmount + delta * 1.5, 1.5);
    
    if (animTime > 0.5) {
      breakBranch(resultType);
      gameState = 'result';
      animTime = 0;
      setTimeout(showResult, 800);
    }
  } else if (gameState === 'springback') {
    // Branch springs back
    branchBendAmount = Math.max(branchBendAmount - delta * 2, 0);
    
    if (animTime > 1.0) {
      gameState = 'result';
      animTime = 0;
      setTimeout(showResult, 300);
    }
  } else if (gameState === 'result') {
    // Fade out effects
    if (resultType !== 'fail') {
      lightTargetIntensity = Math.max(lightTargetIntensity - delta * 0.5, 1.5);
      bloomTarget = Math.max(bloomTarget - delta * 0.3, 0.5);
    }
  }
  
  // Update broken top part physics
  if (brokenTopPart) {
    brokenTopPart.userData.velocity.y -= 5 * delta;
    brokenTopPart.position.add(brokenTopPart.userData.velocity.clone().multiplyScalar(delta));
    brokenTopPart.rotation.x += brokenTopPart.userData.angularVel.x * delta;
    brokenTopPart.rotation.y += brokenTopPart.userData.angularVel.y * delta;
    brokenTopPart.rotation.z += brokenTopPart.userData.angularVel.z * delta;
    
    // Remove when below ground
    if (brokenTopPart.position.y < -2) {
      scene.remove(brokenTopPart);
      brokenTopPart = null;
    }
  }
  
  // Smooth light and bloom
  resultLight.intensity += (lightTargetIntensity - resultLight.intensity) * 0.05;
  bloomPass.strength += (bloomTarget - bloomPass.strength) * 0.05;
  glowMat.opacity += ((resultType !== 'fail' && gameState !== 'idle' ? 0.8 : 0) - glowMat.opacity) * 0.08;
  
  // Gentle camera movement
  camera.position.x = Math.sin(time * 0.1) * 0.3;
  camera.lookAt(0, 3, 0);
  
  composer.render();
}

// ============ Resize ============
window.addEventListener('resize', () => {
  camera.aspect = window.innerWidth / window.innerHeight;
  camera.updateProjectionMatrix();
  renderer.setSize(window.innerWidth, window.innerHeight);
  composer.setSize(window.innerWidth, window.innerHeight);
});

// Add shake animation
const style = document.createElement('style');
style.textContent = `
  @keyframes shake {
    0%, 100% { transform: translateX(0); }
    25% { transform: translateX(-8px); }
    75% { transform: translateX(8px); }
  }
`;
document.head.appendChild(style);

// Start
animate();
