/**
 * Interactive 2D house plan canvas — drag, resize, zoom, grid snap, area calc.
 */
(function () {
    const GRID = 0.5;
    const PIXELS_PER_METER = 18;

    let canvas, ctx;
    let planData = { plotWidth: 20, plotHeight: 15, rooms: [] };
    let scale = 1;
    let offsetX = 40, offsetY = 40;
    let selectedRoom = null;
    let dragMode = null;
    let dragStart = { x: 0, y: 0 };
    let roomStart = {};

    const roomColors = {
        'Hall': '#3d7ab8',
        'Kitchen': '#c47a20',
        'Bedroom': '#4a8f5a',
        'Bathroom': '#6a5a9a',
        'Parking': '#5a5a5a',
        'Staircase': '#8a6a4a',
        'Balcony': '#2a8a9a',
        'default': '#3a6a9a'
    };

    function init() {
        canvas = document.getElementById('planCanvas');
        if (!canvas) return;
        ctx = canvas.getContext('2d');
        const jsonEl = document.getElementById('planJsonData');
        if (jsonEl && jsonEl.value) {
            try {
                planData = JSON.parse(jsonEl.value);
            } catch (e) {
                console.warn('Invalid plan JSON');
            }
        }
        resizeCanvas();
        bindEvents();
        draw();
        updateAreaDisplay();
    }

    function resizeCanvas() {
        const wrap = canvas.parentElement;
        const w = Math.min(wrap.clientWidth - 20, 900);
        const h = Math.min(500, w * 0.65);
        canvas.width = w;
        canvas.height = h;
        draw();
    }

    function metersToPx(m) {
        return m * PIXELS_PER_METER * scale;
    }

    function pxToMeters(px) {
        return px / (PIXELS_PER_METER * scale);
    }

    function snap(v) {
        return Math.round(v / GRID) * GRID;
    }

    function getRoomColor(label) {
        for (const key of Object.keys(roomColors)) {
            if (label.startsWith(key)) return roomColors[key];
        }
        return roomColors.default;
    }

    function draw() {
        if (!ctx) return;
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        drawGrid();
        drawPlotBoundary();
        planData.rooms.forEach((room, i) => drawRoom(room, i));
        drawDimensions();
    }

    function drawGrid() {
        ctx.strokeStyle = 'rgba(74, 158, 255, 0.15)';
        ctx.lineWidth = 1;
        const step = metersToPx(GRID);
        for (let x = offsetX; x < canvas.width; x += step) {
            ctx.beginPath();
            ctx.moveTo(x, 0);
            ctx.lineTo(x, canvas.height);
            ctx.stroke();
        }
        for (let y = offsetY; y < canvas.height; y += step) {
            ctx.beginPath();
            ctx.moveTo(0, y);
            ctx.lineTo(canvas.width, y);
            ctx.stroke();
        }
    }

    function drawPlotBoundary() {
        const pw = metersToPx(planData.plotWidth || 20);
        const ph = metersToPx(planData.plotHeight || 15);
        ctx.strokeStyle = '#f0a500';
        ctx.lineWidth = 3;
        ctx.setLineDash([8, 4]);
        ctx.strokeRect(offsetX, offsetY, pw, ph);
        ctx.setLineDash([]);
        ctx.fillStyle = 'rgba(240, 165, 0, 0.05)';
        ctx.fillRect(offsetX, offsetY, pw, ph);
    }

    function drawRoom(room, index) {
        const x = offsetX + metersToPx(room.x);
        const y = offsetY + metersToPx(room.y);
        const w = metersToPx(room.w);
        const h = metersToPx(room.h);
        const color = getRoomColor(room.label);
        const isSelected = selectedRoom === index;

        ctx.fillStyle = color + (isSelected ? 'cc' : '99');
        ctx.fillRect(x, y, w, h);
        ctx.strokeStyle = isSelected ? '#f0a500' : '#4a9eff';
        ctx.lineWidth = isSelected ? 3 : 2;
        ctx.strokeRect(x, y, w, h);

        ctx.fillStyle = '#fff';
        ctx.font = 'bold 11px Segoe UI';
        ctx.textAlign = 'center';
        ctx.fillText(room.label, x + w / 2, y + h / 2 - 4);
        ctx.font = '10px Segoe UI';
        ctx.fillText(`${room.w.toFixed(1)}×${room.h.toFixed(1)}m`, x + w / 2, y + h / 2 + 10);

        if (room.door !== false) {
            ctx.fillStyle = '#8B4513';
            ctx.fillRect(x + w / 2 - 4, y + h - 3, 8, 3);
        }
        if (room.window !== false) {
            ctx.strokeStyle = '#87CEEB';
            ctx.lineWidth = 2;
            ctx.beginPath();
            ctx.moveTo(x + 2, y + 4);
            ctx.lineTo(x + w - 2, y + 4);
            ctx.stroke();
        }
    }

    function drawDimensions() {
        ctx.fillStyle = '#8ab4d4';
        ctx.font = '11px monospace';
        ctx.textAlign = 'left';
        ctx.fillText(`${planData.plotWidth}m`, offsetX + metersToPx(planData.plotWidth) / 2 - 15, offsetY - 8);
        ctx.save();
        ctx.translate(offsetX - 12, offsetY + metersToPx(planData.plotHeight) / 2);
        ctx.rotate(-Math.PI / 2);
        ctx.fillText(`${planData.plotHeight}m`, 0, 0);
        ctx.restore();
    }

    function hitTest(mx, my) {
        for (let i = planData.rooms.length - 1; i >= 0; i--) {
            const r = planData.rooms[i];
            const x = offsetX + metersToPx(r.x);
            const y = offsetY + metersToPx(r.y);
            const w = metersToPx(r.w);
            const h = metersToPx(r.h);
            if (mx >= x && mx <= x + w && my >= y && my <= y + h) {
                const resizeZone = 12;
                if (mx > x + w - resizeZone && my > y + h - resizeZone) {
                    return { index: i, mode: 'resize' };
                }
                return { index: i, mode: 'move' };
            }
        }
        return null;
    }

    function bindEvents() {
        canvas.addEventListener('mousedown', onMouseDown);
        canvas.addEventListener('mousemove', onMouseMove);
        canvas.addEventListener('mouseup', onMouseUp);
        canvas.addEventListener('mouseleave', onMouseUp);
        window.addEventListener('resize', resizeCanvas);

        document.getElementById('btnZoomIn')?.addEventListener('click', () => { scale = Math.min(2, scale + 0.1); draw(); });
        document.getElementById('btnZoomOut')?.addEventListener('click', () => { scale = Math.max(0.5, scale - 0.1); draw(); });
        document.getElementById('btnGenerate')?.addEventListener('click', generatePlan);
        document.getElementById('btnExportPng')?.addEventListener('click', exportPng);
        document.getElementById('btnSaveLayout')?.addEventListener('click', saveLayout);
    }

    function onMouseDown(e) {
        const rect = canvas.getBoundingClientRect();
        const mx = e.clientX - rect.left;
        const my = e.clientY - rect.top;
        const hit = hitTest(mx, my);
        if (hit) {
            selectedRoom = hit.index;
            dragMode = hit.mode;
            dragStart = { x: mx, y: my };
            const r = planData.rooms[hit.index];
            roomStart = { x: r.x, y: r.y, w: r.w, h: r.h };
            draw();
        } else {
            selectedRoom = null;
            draw();
        }
    }

    function onMouseMove(e) {
        if (selectedRoom === null || !dragMode) return;
        const rect = canvas.getBoundingClientRect();
        const mx = e.clientX - rect.left;
        const my = e.clientY - rect.top;
        const dx = pxToMeters(mx - dragStart.x);
        const dy = pxToMeters(my - dragStart.y);
        const room = planData.rooms[selectedRoom];

        if (dragMode === 'move') {
            room.x = snap(Math.max(0, Math.min(planData.plotWidth - room.w, roomStart.x + dx)));
            room.y = snap(Math.max(0, Math.min(planData.plotHeight - room.h, roomStart.y + dy)));
        } else if (dragMode === 'resize') {
            room.w = snap(Math.max(2, roomStart.w + dx));
            room.h = snap(Math.max(2, roomStart.h + dy));
        }
        preventOverlap(selectedRoom);
        syncJsonField();
        draw();
        updateAreaDisplay();
    }

    function onMouseUp() {
        dragMode = null;
    }

    function preventOverlap(currentIndex) {
        const current = planData.rooms[currentIndex];
        for (let i = 0; i < planData.rooms.length; i++) {
            if (i === currentIndex) continue;
            const other = planData.rooms[i];
            if (rectsOverlap(current, other)) {
                if (dragMode === 'move') {
                    current.x = roomStart.x;
                    current.y = roomStart.y;
                }
            }
        }
    }

    function rectsOverlap(a, b) {
        const gap = 0.3;
        return !(a.x + a.w + gap <= b.x || b.x + b.w + gap <= a.x ||
                 a.y + a.h + gap <= b.y || b.y + b.h + gap <= a.y);
    }

    function syncJsonField() {
        const el = document.getElementById('planJsonData');
        if (el) el.value = JSON.stringify(planData);
    }

    function updateAreaDisplay() {
        let builtUp = 0;
        planData.rooms.forEach(r => { builtUp += r.w * r.h; });
        const plotArea = (planData.plotWidth || 0) * (planData.plotHeight || 0);
        const free = Math.max(0, plotArea - builtUp);
        const builtEl = document.getElementById('statBuiltUp');
        const freeEl = document.getElementById('statFree');
        const plotEl = document.getElementById('statPlot');
        if (builtEl) builtEl.textContent = builtUp.toFixed(1);
        if (freeEl) freeEl.textContent = free.toFixed(1);
        if (plotEl) plotEl.textContent = plotArea.toFixed(1);
    }

    async function generatePlan() {
        const form = document.getElementById('planForm');
        const formData = new FormData(form);
        const body = {};
        formData.forEach((v, k) => {
            if (k === 'kitchen' || k === 'hallRequired' || k === 'parking' || k === 'staircase' || k === 'balcony') {
                body[k] = v === 'true' || v === 'on';
            } else if (['plotWidth', 'plotHeight', 'bedrooms', 'bathrooms', 'floors'].includes(k)) {
                body[k] = parseFloat(v) || parseInt(v, 10);
            } else {
                body[k] = v;
            }
        });
        ['kitchen', 'hallRequired', 'parking', 'staircase', 'balcony'].forEach(f => {
            body[f] = form.querySelector(`[name="${f}"]`)?.checked ?? false;
        });

        try {
            const res = await fetch('/api/generate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
                body: JSON.stringify(body)
            });
            if (!res.ok) throw new Error('Generation failed');
            const data = await res.json();
            planData = JSON.parse(data.planJson);
            syncJsonField();
            draw();
            updateAreaDisplay();
            if (data.areaSummary) {
                document.getElementById('statBuiltUp').textContent = data.areaSummary.totalBuiltUpArea.toFixed(1);
                document.getElementById('statFree').textContent = data.areaSummary.freeArea.toFixed(1);
            }
        } catch (err) {
            alert('Could not generate plan. Check inputs and try again.');
        }
    }

    function exportPng() {
        const link = document.createElement('a');
        link.download = 'house-plan.png';
        link.href = canvas.toDataURL('image/png');
        link.click();
    }

    async function saveLayout() {
        const planId = document.getElementById('planId')?.value;
        if (!planId) return;
        let builtUp = 0;
        planData.rooms.forEach(r => { builtUp += r.w * r.h; });
        const plotArea = planData.plotWidth * planData.plotHeight;
        await fetch(`/api/plans/${planId}/layout`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                planJson: JSON.stringify(planData),
                builtUp,
                free: plotArea - builtUp
            })
        });
        alert('Layout saved.');
    }

    window.PlanCanvas = { init, setPlanData: (d) => { planData = d; draw(); updateAreaDisplay(); } };
    document.addEventListener('DOMContentLoaded', init);
})();
