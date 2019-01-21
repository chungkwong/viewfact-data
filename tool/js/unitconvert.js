var unitconvertWidget = function (selector, units) {
	var widget = document.querySelector(selector);
	var controls = [];
	var changing = false;
	for (var unit in units) {
		var control = document.createElement('input');
		control.type = 'number';
		control.step = 'any';
		control.value = 0;
		control.unitValue = units[unit];
		control.oninput = function (e) {
			if (!changing) {
				changing = true;
				var refValue = e.target.value * e.target.unitValue;
				for (var c in controls) {
					if (controls[c] !== e.target) {
						controls[c].value = refValue / controls[c].unitValue;
					}
				}
				changing = false;
			}
		};
		widget.appendChild(control);
		var label = document.createElement('label');
		label.textContent = unit;
		widget.appendChild(label);
		widget.appendChild(document.createElement('br'));
		controls.push(control);
	}
};
var unitconvertAffineWidget = function (selector, units) {
	var widget = document.querySelector(selector);
	var controls = [];
	var changing = false;
	for (var unit in units) {
		var control = document.createElement('input');
		control.type = 'number';
		control.step = 'any';
		control.value = units[unit][1];
		control.unitValue = units[unit][0];
		control.offsetValue = units[unit][1];
		control.oninput = function (e) {
			if (!changing) {
				changing = true;
				var refValue = (e.target.value - e.target.offsetValue) / e.target.unitValue;
				for (var c in controls) {
					if (controls[c] !== e.target) {
						controls[c].value = refValue * controls[c].unitValue + controls[c].offsetValue;
					}
				}
				changing = false;
			}
		};
		widget.appendChild(control);
		var label = document.createElement('label');
		label.textContent = unit;
		widget.appendChild(label);
		widget.appendChild(document.createElement('br'));
		controls.push(control);
	}
};