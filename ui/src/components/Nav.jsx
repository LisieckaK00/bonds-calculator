import { Breadcrumbs } from '@mui/material'
import { Link } from 'react-router-dom';

function handleClick(event) {
    event.preventDefault();
}

const data = ['OVERVIEW', 'OTS', 'ROR', 'DOR', 'TOS', 'COI', 'EDO', 'ROS', 'ROD'];

const breadcrumbData = data.map(item => ({
    label: item,
    href: `/${item.toLowerCase()}`,
}));

export default function Nav() {
    return (
        <>
        <div style={{ display: 'flex', justifyContent: 'center', marginTop: 40 }}>
            <div role="presentation" onClick={handleClick}>
            <Breadcrumbs maxItems={15} sx={{ fontSize: 24 }}>
                <Link
                        underline="hover"
                        to="/info"
                        style={{
                            cursor: 'pointer', 
                            textDecoration: 'none', 
                        }}
                >INFO</Link>
                {breadcrumbData.map((breadcrumb, index) => (
                <Link
                    key={index}
                    underline="hover"
                    color={breadcrumb.current ? 'primary' : 'text.primary'}
                    to={breadcrumb.href}
                    aria-current={breadcrumb.current ? 'page' : undefined}
                    style={{
                        color: breadcrumb.current ? undefined : 'white', 
                        cursor: 'pointer', 
                        textDecoration: 'none', 
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