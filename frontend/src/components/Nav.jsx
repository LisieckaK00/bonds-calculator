import { Breadcrumbs } from '@mui/material'
import { Link } from 'react-router-dom';

function handleClick(event) {
    event.preventDefault();
    console.info('You clicked a breadcrumb.');
}

const data = ['OVERVIEW', 'OTS', 'ROR', 'DOR', 'TOS', 'COI', 'EDO', 'ROS', 'ROD'];

const breadcrumbData = data.map(item => ({
  label: item,
  href: `/${item.toLowerCase()}`, 
  current: item === 'OVERVIEW' 
}));

export default function Nav() {
    return (
        <>
        <div style={{ display: 'flex', justifyContent: 'center', marginTop: 40 }}>
            <div role="presentation" onClick={handleClick}>
            <Breadcrumbs maxItems={15} aria-label="breadcrumb" sx={{ fontSize: 24 }}>
                {breadcrumbData.map((breadcrumb, index) => (
                <Link
                    key={index}
                    underline="hover"
                    color={breadcrumb.current ? 'primary' : 'text.primary'}
                    to={breadcrumb.href} // Użyj "to" zamiast "href"
                    aria-current={breadcrumb.current ? 'page' : undefined}
                    style={{
                        color: breadcrumb.current ? undefined : 'white', 
                        cursor: 'pointer', 
                        textDecoration: 'none', 
                        fontWeight: breadcrumb.current ? 600 : 400
                    }}
                >
                {breadcrumb.label}
                </Link>
                ))}
            </Breadcrumbs>
            </div>
        </div>
        </>
    )
}