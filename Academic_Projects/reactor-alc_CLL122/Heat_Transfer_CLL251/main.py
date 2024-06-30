import numpy as np
import matplotlib.pyplot as plt

k = 0.012  # Thermal Conductivity (W/m.K)
L = 0.27   # Length (m)
m = 1.16   # Mass (kg)
c = 4182   # Specific heat (J/kg.K)
a = 0.03   # Inner radius (m)
b = 0.06   # Outer radius (m)
t = np.arange(0, 10.1, 0.1)  # Time (hr)

factor = (2 * np.pi * k * L) / (m * c * np.log(b / a))  # dt factor

numerical = np.array([90.0000, 89.8578, 89.7166, 89.5764, 89.4366, 89.2975, 89.1599, 89.0223,
                      88.8848, 88.7472, 88.6122, 88.4787, 88.3452, 88.2118, 88.0783, 87.9449, 87.8114,
                      87.6779, 87.5445, 87.4121, 87.2827, 87.1534, 87.0241, 86.8947, 86.7654, 86.6361,
                      86.5067, 86.3774, 86.2481, 86.1197, 85.9943, 85.8688, 85.7434, 85.6179, 85.4925,
                      85.3670, 85.2416, 85.1161, 84.9907, 84.8661, 84.7440, 84.6219, 84.4998, 84.3777,
                      84.2556, 84.1335, 84.0114, 83.8893, 83.7672, 83.6460, 83.5274, 83.4088, 83.2902,
                      83.1716, 83.0530, 82.9344, 82.8158, 82.6972, 82.5786, 82.4603, 82.3432, 82.2264,
                      82.1098, 81.9935, 81.8774, 81.7616, 81.6461, 81.5308, 81.4158, 81.3011, 81.1868,
                      81.0728, 80.9590, 80.8455, 80.7324, 80.6195, 80.5069, 80.3946, 80.2826, 80.1709,
                      80.0595, 79.9485, 79.8377, 79.7273, 79.6172, 79.5073, 79.3978, 79.2886, 79.1796,
                      79.0710, 78.9627, 78.8546, 78.7469, 78.6395, 78.5323, 78.4255, 78.3190, 78.2127,
                      78.1068, 78.0012, 77.8958])

# Numerical results from COMSOL
T = np.zeros_like(t)
for i in range(len(t)):
    T[i] = (65 / np.exp(factor * t[i] * 3600)) + 25

plt.plot(t, T, label='Analytical Temperature')
plt.plot(t, numerical, '--', label='Numerical Temperature')
plt.ylabel('Temperature (C)')
plt.xlabel('Time (hr)')
plt.title('Temperature Distribution')
plt.legend()
plt.show()
