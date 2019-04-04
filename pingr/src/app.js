const headrs = new Headers();
headrs.append('cache-control', 'no-cache');

const init = {
    method: `GET`,
    headers: headrs
}

function ping() {
    console.log(`ping`);
    let t0 = new Date().getTime();
    fetch(`/`, init)
        .then(data => data.text())
        .then(text => {
            let t1 = new Date().getTime();
            let rtt = t1 - t0;
            let el_rtt = document.getElementById(`rtt`);
            el_rtt.textContent = `${rtt}`;
            console.log(`rtt: ${rtt}`);

        })
        .catch(error => console.error(error));
}

setInterval(ping, 1000);