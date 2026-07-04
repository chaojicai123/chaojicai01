const fs = require('fs/promises');
const base = 'http://localhost:8080/api';

async function req(path, options = {}){
  const url = base + path;
  const res = await fetch(url, options);
  const text = await res.text();
  let body;
  try { body = JSON.parse(text); } catch(e) { body = text; }
  if (!res.ok) throw { status: res.status, body };
  return body;
}

async function post(path, json, token){
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers.Authorization = `Bearer ${token}`;
  return await req(path, { method: 'POST', headers, body: JSON.stringify(json) });
}

async function patch(path, json, token){
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers.Authorization = `Bearer ${token}`;
  return await req(path, { method: 'PATCH', headers, body: JSON.stringify(json) });
}

async function get(path, token){
  const headers = {};
  if (token) headers.Authorization = `Bearer ${token}`;
  return await req(path, { method: 'GET', headers });
}

function nowDate(){
  const d = new Date();
  return d.toISOString().split('T')[0];
}

(async ()=>{
  try{
    // 1. register tester
    const reg = await post('/auth/register', { username: 'tester1', password: 'tester123', realName: 'Tester', role: 'FAMILY' });
    await fs.writeFile('./tester_register.json', JSON.stringify(reg, null, 2), 'utf8');

    // 2. admin login
    const admin = await post('/auth/login', { username: 'admin', password: 'admin123' });
    await fs.writeFile('./admin_login.json', JSON.stringify(admin, null, 2), 'utf8');
    const adminToken = admin.token;

    // 3. create elder
    const elder = await post('/elders', { name: 'ElderTest', gender: 'M' }, adminToken);
    await fs.writeFile('./elder_create.json', JSON.stringify(elder, null, 2), 'utf8');

    // 4. link tester to elder
    const userId = reg.userId || reg.id || reg.user?.id;
    if (!userId) throw new Error('无法从注册响应获取 userId');
    await patch(`/admin/users/${userId}/linked-elder`, { linkedElderId: elder.id }, adminToken);
    await fs.writeFile('./link_done.txt', 'linked', 'utf8');

    // 5. tester login
    const tester = await post('/auth/login', { username: 'tester1', password: 'tester123' });
    await fs.writeFile('./tester_login.json', JSON.stringify(tester, null, 2), 'utf8');
    const testerToken = tester.token;

    // 6. create a health record (by admin)
    const hrBody = { elder: { id: elder.id }, recordDate: nowDate(), bloodPressureHigh: '120', bloodPressureLow: '80', heartRate: '70' };
    const hr = await post('/health', hrBody, adminToken);
    await fs.writeFile('./health_create.json', JSON.stringify(hr, null, 2), 'utf8');

    // 7. admin list
    const adminList = await get(`/health/elder/${elder.id}?page=0&size=10`, adminToken);
    await fs.writeFile('./admin_health_list.json', JSON.stringify(adminList, null, 2), 'utf8');

    // 8. tester list
    const testerList = await get(`/health/elder/${elder.id}?page=0&size=10`, testerToken);
    await fs.writeFile('./tester_health_list.json', JSON.stringify(testerList, null, 2), 'utf8');

    console.log('DONE');
  }catch(err){
    try{ await fs.writeFile('./error_output.json', JSON.stringify(err, null, 2), 'utf8'); }catch(e){}
    console.error('ERROR', err);
    process.exit(1);
  }
})();
